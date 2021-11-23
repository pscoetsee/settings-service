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
import com.pcoetsee.settingservice.exception.IllegalAccessException;
import com.pcoetsee.settingservice.exception.NoResultsException;
import com.pcoetsee.settingservice.exception.RecordCreationException;
import com.pcoetsee.settingservice.exception.ServiceDoesNotExistException;
import com.pcoetsee.settingservice.persistence.dao.Role;
import com.pcoetsee.settingservice.persistence.dao.ServiceDAO;
import com.pcoetsee.settingservice.persistence.repository.ServiceRepository;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZonedDateTime;

/**
 * This service is used for processing data relating to the `settings_service`.`services` table.
 */
@Service
public class ServicesService implements UserDetailsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServicesService.class);

    private final ServiceRepository serviceRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public ServicesService(
            @Autowired ServiceRepository serviceRepository,
            @Autowired BCryptPasswordEncoder bCryptPasswordEncoder
    ) {
        this.serviceRepository = serviceRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
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
        serviceDTO.setRole(serviceDAO.getRole().name());
        serviceDTO.setCreationTime(ZonedDateTime.ofInstant(Instant.ofEpochMilli(serviceDAO.getCreationTime()), ZonedDateTime.now().getZone()));

        return serviceDTO;
    }

    /**
     * This method converts a service transfer object to a service data access object.
     *
     * @param serviceDTO the ServiceDTO to conver to a ServiceDAO, null returns null
     * @return a ServiceDAO object representation of the supplied ServiceDTO, may return null
     */
    public static ServiceDAO serviceDTOToDAO(ServiceDTO serviceDTO, BCryptPasswordEncoder bCryptPasswordEncoder) {
        if (serviceDTO == null) {
            return null;
        }

        ServiceDAO serviceDAO = new ServiceDAO();

        serviceDAO.setName(serviceDTO.getName());
        serviceDAO.setPassword(bCryptPasswordEncoder.encode(serviceDTO.getPassword()));
        serviceDAO.setRole(Role.roleFromString(serviceDTO.getRole()));

        return serviceDAO;
    }

    /**
     * This method will fetch a service matching the supplied name, as a DTO, limiting the fields that are returned.
     *
     * @param name the name of the service that we want details for, null causes exception
     * @return a service matching the supplied name if found, exception if none found
     * @throws IllegalArgumentException     when the supplied name is blank or null
     * @throws ServiceDoesNotExistException if not service with a matching name is found
     */
    public ServiceDTO getServiceMatchingName(String name) throws IllegalArgumentException, ServiceDoesNotExistException {
        return ServicesService.serviceDAOToDTO(this.getServiceDAOMatchingName(name));
    }

    /**
     * This method will find all available services. Allows for paging.
     *
     * @param pageable this object allows for paging, null returns all results
     * @return a Page of all services as limited by the pageable parameter, not null
     * @throws NoResultsException when no results are found
     */
    public Page<ServiceDTO> getAllServices(Pageable pageable) throws NoResultsException {
        return this.getAllServicesDAO(pageable).map(ServicesService::serviceDAOToDTO);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            return (UserDetails) this.getServiceMatchingName(username);
        } catch (Exception e) {
            throw new UsernameNotFoundException(e.getMessage());
        }
    }

    /**
     * This method handles the creation of new services.
     *
     * @param serviceDTO object containing all fields needed to create a new service, null throws exception
     * @return the newly created service, not null
     * @throws IllegalArgumentException when no data is supplied, or when an invalid username or password is used
     * @throws RecordCreationException  when all other checks are passed, but the record can still not be created
     */
    public ServiceDTO createService(ServiceDTO serviceDTO) throws IllegalArgumentException, RecordCreationException {
        if (serviceDTO == null) {
            throw new IllegalArgumentException("No valid parameters supplied");
        }

        if (StringUtils.isBlank(serviceDTO.getName())) {
            throw new IllegalArgumentException("No name supplied, can not create new service");
        }

        if (StringUtils.isBlank(serviceDTO.getPassword())) {
            throw new IllegalArgumentException("No password supplied, can not create new service");
        }

        ServiceDAO serviceDAO = this.serviceRepository.findByName(serviceDTO.getName());

        if (serviceDAO != null) {
            throw new IllegalArgumentException("A service with the supplied name already exists");
        }

        serviceDAO = this.serviceRepository.save(ServicesService.serviceDTOToDAO(serviceDTO, this.bCryptPasswordEncoder));

        if (serviceDAO.getId() == null) {
            throw new RecordCreationException();
        }

        return ServicesService.serviceDAOToDTO(serviceDAO);
    }

    public boolean updateService(String password, ServiceDTO serviceDTO) throws IllegalArgumentException, IllegalAccessException {
        if (serviceDTO == null) {
            throw new IllegalArgumentException("No valid parameters supplied.");
        }
        if (StringUtils.isBlank(serviceDTO.getName())) {
            throw new IllegalArgumentException("No name supplied, can't figure out which service to update.");
        }

        ServiceDAO serviceDAO = this.serviceRepository.findByName(serviceDTO.getName());

        if (serviceDAO == null) {
            throw new IllegalArgumentException("No service found with specified name, can not update unknown service");
        }

        ServiceDAO updateServiceDAO = ServicesService.serviceDTOToDAO(serviceDTO, this.bCryptPasswordEncoder);

        String service = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean hasFullRole = SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains(Role.FULL.getAuthority());

        if (!isUpdateAllowed(updateServiceDAO, service, hasFullRole, ) {
            throw new IllegalAccessException();
        }
    }

    /**
     * This method will fetch a service matching the supplied name.
     *
     * @param name the name of the service that we want details for, null causes exception
     * @return a service matching the supplied name if found, exception if none found
     * @throws IllegalArgumentException     when the supplied name is blank or null
     * @throws ServiceDoesNotExistException if not service with a matching name is found
     */
    private ServiceDAO getServiceDAOMatchingName(String name) throws IllegalArgumentException, ServiceDoesNotExistException {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("Cannot match service, null or empty name supplied.");
        }

        ServiceDAO result = this.serviceRepository.findByName(name);

        if (result == null) {
            throw new ServiceDoesNotExistException();
        }

        return result;
    }

    /**
     * This method will find all available services. Allows for paging.
     *
     * @param pageable this object allows for paging, null returns all results
     * @return a Page of all services as limited by the pageable parameter, not null
     * @throws NoResultsException when no results are found
     */
    private Page<ServiceDAO> getAllServicesDAO(Pageable pageable) throws NoResultsException {
        Page<ServiceDAO> returnValue = this.serviceRepository.findAll(pageable);

        if (returnValue == null || returnValue.isEmpty()) {
            throw new NoResultsException();
        }

        return returnValue;
    }

    /**
     * This method checks whether modifications are allowed. Modification is only allowed if the service to be modified
     * is the same service making the request, or if the service making the request is has the {@link Role#FULL} role.
     *
     * @param updateDAO     a {@link ServiceDAO} that should contain the name of the service to be modified, null causes
     *                      exception
     * @param authedService the name of the currently authenticated service, blank or null causes exception
     * @param hasFullRole   indicates whether the currently authenticated service has the {@link Role#FULL} role, not
     *                      null
     * @return true if currently authenticated service is allowed to make the supplied updates, otherwise false
     */
    private boolean isUpdateAllowed(ServiceDAO updateDAO, String authedService, boolean hasFullRole) throws IllegalArgumentException, IllegalAccessException {
        if (updateDAO == null || StringUtils.isBlank(updateDAO.getName())) {
            throw new IllegalArgumentException("Service to be modified not specified, can not make modifications.");
        }

        if (StringUtils.equalsIgnoreCase(updateDAO.getName(), authedService)) {
            return true;
        }

        if (!hasFullRole) {
            throw new IllegalAccessException();
        }

        return true;
    }

    /**
     * This method checks whether an update request will allow for the password to be updated. It does not check whether
     * the person making the request is allowed to update the password field.
     * <p>
     * If a new password is passed, and it does not exist the existing password, we will attempt to change it.
     *
     * @param updateDAO a {@link ServiceDAO} that should contain a modified password, null returns false
     * @param oldDAO    a {@link ServiceDAO} that contains the existing password, null returns false
     * @return true if updateDAO contains a password and it does not match the password in oldDAO, false otherwise
     */
    private boolean isUpdatePassword(ServiceDAO updateDAO, ServiceDAO oldDAO) {
        if (updateDAO == null || oldDAO == null) {
            return false;
        }
        if (StringUtils.isBlank(updateDAO.getPassword())) {
            return false;
        }

        return !StringUtils.equalsIgnoreCase(updateDAO.getPassword(), oldDAO.getPassword());
    }

    /**
     * This method checks whether the currently authenticated service is allowed to change the password for the supplied
     * service.
     * <p>
     * The authenticated service is only allowed to modify the password if it has the {@link Role#FULL} role, or if the
     * service being modified is the same as the currently authenticated one. As an added precaution for the latter case
     * the request must also contain the current password, and it must match the existing one.
     *
     * @param updateDAO     a {@link ServiceDAO} containing the update details, will be checked for the name of the service
     *                      being updated, null safe
     * @param oldDAO        a {@link ServiceDAO} containing the existing password, null safe
     * @param hasFullRole   indicates whether the currently authenticated service has the {@link Role#FULL} role, not
     *                      null
     * @param authedService the name of the currently authenticated services, used to determine if a service is `
     * @param oldPassword   the password currently being used for the service being modified, null safe
     * @return true only if the currently authenticated user is allowed to update the password as specified by the
     * updateDTO, otherwise false
     */
    private boolean isPasswordUpdateAllowed(ServiceDAO updateDAO, ServiceDAO oldDAO, boolean hasFullRole, String authedService, String oldPassword) {
        if (updateDAO == null || StringUtils.isBlank(updateDAO.getName())) {
            return false;
        }

        if (hasFullRole) {
            return true;
        }

        return oldDAO != null && StringUtils.equalsIgnoreCase(oldDAO.getPassword(), this.bCryptPasswordEncoder.encode(oldPassword));
    }
}
