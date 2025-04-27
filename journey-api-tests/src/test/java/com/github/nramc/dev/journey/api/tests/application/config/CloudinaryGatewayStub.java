package com.github.nramc.dev.journey.api.tests.application.config;

import com.cloudinary.Cloudinary;
import com.github.nramc.dev.journey.api.gateway.cloudinary.CloudinaryGateway;
import com.github.nramc.dev.journey.api.gateway.cloudinary.CloudinaryProperties;

public class CloudinaryGatewayStub extends CloudinaryGateway {

    public CloudinaryGatewayStub(Cloudinary cloudinary, CloudinaryProperties cloudinaryProperties) {
        super(cloudinary, cloudinaryProperties);
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

}
