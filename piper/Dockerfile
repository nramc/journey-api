FROM python:3.14-slim

# Install dependencies
RUN apt-get update && apt-get install --yes --no-install-recommends \
    wget \
    && rm -rf /var/lib/apt/lists/*

# Install piper with HTTP support
RUN pip install piper-tts[http]

# Create model directory
WORKDIR /app

# Download model at build time ✅
RUN python3 -m piper.download_voices en_US-lessac-medium

# Expose port
EXPOSE 5000

# Start server
CMD ["python3", "-m", "piper.http_server", "-m", "en_US-lessac-medium"]