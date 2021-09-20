/*
 * MIT License
 *
 * Copyright (c) 2021 PS Coetsee
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.pcoetsee.settingservice.service;

import com.pcoetsee.settingservice.dto.ServiceDTO;
import com.pcoetsee.settingservice.persistence.dao.ServiceDAO;
import com.pcoetsee.settingservice.persistence.repository.ServiceRepository;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZonedDateTime;

/**
 * This service is used for processing data relating to the `settings_service`.`services` table.
 */
@Service
public class ServicesService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServicesService.class);

    private final ServiceRepository serviceRepository;

    public ServicesService(@Autowired ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    /**
     * This method converts a service data access object to a service data transfer object.
     *
     * @param serviceDAO the ServiceDAO to concert to a ServiceDTO object, null returns null
     * @return a ServiceDTO object representation of the supplied ServiceDAO object, may return null
     */
    public static ServiceDTO serviceDAOToDTO(ServiceDAO serviceDAO) {
        if (serviceDAO == null) {
            return null;
        }

        ServiceDTO serviceDTO = new ServiceDTO();

        serviceDTO.setName(serviceDAO.getName());
        serviceDTO.setAdmin(serviceDAO.isAdmin());
        serviceDTO.setCreationTime(ZonedDateTime.ofInstant(Instant.ofEpochMilli(serviceDAO.getCreationTime()), ZonedDateTime.now().getZone()));

        return serviceDTO;
    }

    public ServiceDTO getServiceMatchingNameAndPassword(String name, String password) throws IllegalArgumentException {
        return ServicesService.serviceDAOToDTO(this.getServiceDAOMatchingNameAndPassword(name, password));
    }

    public ServiceDAO getServiceDAOMatchingNameAndPassword(String name, String password) throws IllegalArgumentException {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("Cannot match service, null or empty name supplied.");
        }

        if (StringUtils.isBlank(password)) {
            throw new IllegalArgumentException("Cannot match service, null or empty password supplied.");
        }

        return this.serviceRepository.getServiceDAOByNameAndPassword(name, password);
    }
}
