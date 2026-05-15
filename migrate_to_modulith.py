#!/usr/bin/env python3
"""Comprehensive Spring Modulith migration script for Journey API.

Moves source files into the 6-module structure:
  shared / journey / account / notification / ai / infrastructure
and updates all package declarations + imports.
"""
import re
import sys
from pathlib import Path

BASE = Path("/Users/rnellaiyappan/IdeaProjects/journey-api")
SRC = BASE / "journey-api-web/src/main/java"
TEST_SRC = BASE / "journey-api-web/src/test/java"
ROOT = SRC / "com/github/nramc/dev/journey/api"
ROOT_PKG = "com.github.nramc.dev.journey.api"

# ---------------------------------------------------------------------------
# Directory moves: (old_relative_to_ROOT, new_relative_to_ROOT)
# All *.java files inside old_dir are moved to new_dir preserving sub-structure
# ---------------------------------------------------------------------------
DIR_MOVES = [
    # SHARED
    ("core/domain",             "shared/domain"),
    ("core/exceptions",         "shared/exceptions"),
    ("core/utils",              "shared/utils"),
    ("core/validation",         "shared/validation"),
    # JOURNEY
    ("core/journey",            "journey/domain"),
    ("repository/journey",      "journey/repository"),
    ("gateway/cloudinary",      "journey/gateway/cloudinary"),
    ("core/app/health",         "journey/health"),
    ("web/resources/rest/journeys",   "journey/web/journeys"),
    ("web/resources/rest/statistics", "journey/web/statistics"),
    ("web/resources/rest/timeline",   "journey/web/timeline"),
    # ACCOUNT (user + auth combined)
    ("repository/user",              "account/repository"),
    ("core/usecase/registration",    "account/usecase"),
    ("core/usecase/codes",           "account/codes"),
    ("core/jwt",                     "account/jwt"),
    ("core/security/webauthn",       "account/webauthn"),
    ("web/resources/rest/auth",      "account/web/auth"),
    ("web/resources/rest/api",       "account/web/api"),
    ("web/resources/rest/users",     "account/web/users"),
    # NOTIFICATION gateway + mail (notification/*.java handled as individual moves)
    ("gateway/telegram",             "notification/gateway/telegram"),
    ("core/services/mail",           "notification/mail"),
    # AI
    ("web/resources/rest/ai",        "ai/web"),
    # INFRASTRUCTURE
    ("config/security",              "infrastructure/security"),
    ("repository/converters",        "infrastructure/mongodb/converters"),
    ("config/timezone",              "infrastructure/timezone"),
    ("migration",                    "infrastructure/migration"),
    ("web/exceptions",               "infrastructure/web"),
    ("web/resources/mvc",            "infrastructure/web/mvc"),
]

# ---------------------------------------------------------------------------
# Individual file moves
# ---------------------------------------------------------------------------
FILE_MOVES = [
    # shared
    ("web/resources/Resources.java",
     "shared/web/Resources.java"),
    ("web/resources/rest/doc/RestDocCommonResponse.java",
     "shared/web/doc/RestDocCommonResponse.java"),
    # infrastructure
    ("core/app/ApplicationProperties.java",
     "infrastructure/actuator/ApplicationProperties.java"),
    ("config/MongoConfig.java",
     "infrastructure/mongodb/MongoConfig.java"),
    ("config/OpenApiDocumentationConfig.java",
     "infrastructure/openapi/OpenApiDocumentationConfig.java"),
    # notification — three files split to different sub-packages
    ("core/usecase/notification/NotificationService.java",
     "notification/NotificationService.java"),
    ("core/usecase/notification/EmailNotificationService.java",
     "notification/email/EmailNotificationService.java"),
    ("core/usecase/notification/TelegramNotificationService.java",
     "notification/telegram/TelegramNotificationService.java"),
]

# ---------------------------------------------------------------------------
# Old config files to delete (beans are redistributed to module configs)
# ---------------------------------------------------------------------------
FILES_TO_DELETE = [
    "config/ApplicationUseCaseConfig.java",
    "config/ApplicationServiceConfig.java",
    "config/ApplicationContextConfig.java",
    "config/ApplicationActuatorConfig.java",
    "config/TotpConfig.java",
    "config/cloudinary/CloudinaryConfig.java",
    "config/MailConfig.java",
    "config/telegram/TelegramConfig.java",
]

# ---------------------------------------------------------------------------
# Import replacement mappings applied to EVERY *.java file
# Order matters: more specific patterns first
# ---------------------------------------------------------------------------
R = ROOT_PKG
IMPORT_MAPPINGS = [
    # --- notification (specific class patterns must come before package patterns) ---
    (f"{R}.core.usecase.notification.NotificationService",      f"{R}.notification.NotificationService"),
    (f"{R}.core.usecase.notification.EmailNotificationService", f"{R}.notification.email.EmailNotificationService"),
    (f"{R}.core.usecase.notification.TelegramNotificationService", f"{R}.notification.telegram.TelegramNotificationService"),
    # --- shared ---
    (f"{R}.core.domain",      f"{R}.shared.domain"),
    (f"{R}.core.exceptions",  f"{R}.shared.exceptions"),
    (f"{R}.core.utils",       f"{R}.shared.utils"),
    (f"{R}.core.validation",  f"{R}.shared.validation"),
    (f"{R}.web.resources.Resources", f"{R}.shared.web.Resources"),
    (f"{R}.web.resources.rest.doc",  f"{R}.shared.web.doc"),
    # --- journey ---
    (f"{R}.core.journey",                 f"{R}.journey.domain"),
    (f"{R}.repository.journey",           f"{R}.journey.repository"),
    (f"{R}.gateway.cloudinary",           f"{R}.journey.gateway.cloudinary"),
    (f"{R}.core.app.health",              f"{R}.journey.health"),
    (f"{R}.web.resources.rest.journeys",  f"{R}.journey.web.journeys"),
    (f"{R}.web.resources.rest.statistics",f"{R}.journey.web.statistics"),
    (f"{R}.web.resources.rest.timeline",  f"{R}.journey.web.timeline"),
    # --- account ---
    (f"{R}.repository.user",           f"{R}.account.repository"),
    (f"{R}.core.usecase.registration", f"{R}.account.usecase"),
    (f"{R}.core.usecase.codes",        f"{R}.account.codes"),
    (f"{R}.core.jwt",                  f"{R}.account.jwt"),
    (f"{R}.core.security.webauthn",    f"{R}.account.webauthn"),
    (f"{R}.web.resources.rest.auth",   f"{R}.account.web.auth"),
    (f"{R}.web.resources.rest.api",    f"{R}.account.web.api"),
    (f"{R}.web.resources.rest.users",  f"{R}.account.web.users"),
    # --- notification (remaining package-level) ---
    (f"{R}.gateway.telegram",          f"{R}.notification.gateway.telegram"),
    (f"{R}.core.services.mail",        f"{R}.notification.mail"),
    # --- ai ---
    (f"{R}.web.resources.rest.ai",     f"{R}.ai.web"),
    # --- infrastructure ---
    (f"{R}.config.security",           f"{R}.infrastructure.security"),
    (f"{R}.repository.converters",     f"{R}.infrastructure.mongodb.converters"),
    (f"{R}.config.timezone",           f"{R}.infrastructure.timezone"),
    (f"{R}.migration",                 f"{R}.infrastructure.migration"),
    (f"{R}.web.exceptions",            f"{R}.infrastructure.web"),
    (f"{R}.web.resources.mvc",         f"{R}.infrastructure.web.mvc"),
    (f"{R}.core.app.ApplicationProperties", f"{R}.infrastructure.actuator.ApplicationProperties"),
    (f"{R}.config.MongoConfig",        f"{R}.infrastructure.mongodb.MongoConfig"),
    (f"{R}.config.OpenApiDocumentationConfig", f"{R}.infrastructure.openapi.OpenApiDocumentationConfig"),
]


# ===========================================================================
# Helpers
# ===========================================================================

def pkg_from_path(file_path: Path) -> str:
    return str(file_path.parent.relative_to(SRC)).replace("/", ".")


def move_file(old_path: Path, new_path: Path) -> None:
    if not old_path.exists():
        print(f"  WARNING: {old_path.relative_to(BASE)} not found, skipping")
        return
    new_path.parent.mkdir(parents=True, exist_ok=True)
    content = old_path.read_text(encoding="utf-8")

    # Replace the package statement
    new_pkg = pkg_from_path(new_path)
    content = re.sub(
        r"^(package\s+)([\w.]+)(\s*;)",
        lambda m: f"{m.group(1)}{new_pkg}{m.group(3)}",
        content, count=1, flags=re.MULTILINE
    )
    new_path.write_text(content, encoding="utf-8")
    old_path.unlink()

    # Remove now-empty parent directories bottom-up
    p = old_path.parent
    while p != ROOT:
        try:
            p.rmdir()
            p = p.parent
        except OSError:
            break

    print(f"  {old_path.relative_to(ROOT)} → {new_path.relative_to(ROOT)}")


def move_directory(old_dir: Path, new_dir: Path) -> None:
    if not old_dir.exists():
        print(f"  WARNING dir: {old_dir.relative_to(BASE)} not found, skipping")
        return
    for old_file in sorted(old_dir.rglob("*.java")):
        rel = old_file.relative_to(old_dir)
        move_file(old_file, new_dir / rel)


def apply_imports(file_path: Path, mappings: list[tuple[str, str]]) -> None:
    try:
        content = file_path.read_text(encoding="utf-8")
        updated = content
        for old, new in mappings:
            updated = updated.replace(old, new)
        if updated != content:
            file_path.write_text(updated, encoding="utf-8")
    except Exception as exc:
        print(f"  WARN import update failed for {file_path}: {exc}")


# ===========================================================================
# MAIN
# ===========================================================================

print("=" * 60)
print("  Spring Modulith Migration — Journey API")
print("=" * 60)

# --- Step 1: move directories ----------------------------------------
print("\n[1/4] Moving directories …")
for old_rel, new_rel in DIR_MOVES:
    print(f"\n  {old_rel} → {new_rel}")
    move_directory(ROOT / old_rel, ROOT / new_rel)

# --- Step 2: move individual files -----------------------------------
print("\n[2/4] Moving individual files …")
for old_rel, new_rel in FILE_MOVES:
    move_file(ROOT / old_rel, ROOT / new_rel)

# --- Step 3: delete old config stubs ---------------------------------
print("\n[3/4] Deleting obsolete config files …")
for rel in FILES_TO_DELETE:
    p = ROOT / rel
    if p.exists():
        p.unlink()
        print(f"  Deleted: {rel}")
        # prune empty parent
        parent = p.parent
        while parent != ROOT:
            try:
                parent.rmdir()
                parent = parent.parent
            except OSError:
                break
    else:
        print(f"  Already absent: {rel}")

# --- Step 4: update imports everywhere --------------------------------
print("\n[4/4] Updating imports across all Java sources …")
all_java = list(SRC.rglob("*.java")) + list(TEST_SRC.rglob("*.java"))
changed = 0
for jf in all_java:
    before = jf.read_text(encoding="utf-8")
    after = before
    for old, new in IMPORT_MAPPINGS:
        after = after.replace(old, new)
    if after != before:
        jf.write_text(after, encoding="utf-8")
        changed += 1
print(f"  Updated imports in {changed}/{len(all_java)} files")

print("\n✅  Migration complete!")
print("   Next: create package-info.java, module configs, events, and new tests.")

