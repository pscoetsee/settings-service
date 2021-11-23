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

package com.pcoetsee.settingservice.persistence.repository;

import com.pcoetsee.settingservice.persistence.dao.Role;
import com.pcoetsee.settingservice.persistence.dao.ServiceDAO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.ZonedDateTime;

/**
 * Class used for testing the repository accessing the `services` table in the database.
 * <p>
 * Tests all general crud operations. Reads are tested in most other methods, as they are used confirmation of results.
 */
@RunWith(SpringRunner.class)
@DataJpaTest()
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE, replace = AutoConfigureTestDatabase.Replace.NONE)
public class ServiceRepositoryTest {

    /**
     * Name to the used for the first service,
     */
    private static final String FIRST_SERVICE_NAME = "firstService";
    /**
     * Password to be used for the first service.
     */
    private static final String FIRST_SERVICE_PASSWORD = "firstPassword";
    /**
     * Name to be used for the second service.
     */
    private static final String SECOND_SERVICE_NAME = "secondService";
    /**
     * Password to be used for the second service.
     */
    private static final String SECOND_SERVICE_PASSWORD = "secondPassword";
    /**
     * Name to use when testing update.
     */
    private static final String UPDATE_SERVICE_NAME = "updateService";
    /**
     * Password to use wen testing update
     */
    private static final String UPDATE_SERVICE_PASSWORD = "updatePassword";

    @Autowired
    private ServiceRepository serviceRepository;

    /**
     * This method sets up data for testing, one normal user and one admin user.
     */
    @Before
    public void init() {
        ServiceDAO serviceDAO = new ServiceDAO();

        //CREATE OUR FIRST TEST USER AS A REGULAR USER
        serviceDAO.setName(ServiceRepositoryTest.FIRST_SERVICE_NAME);
        serviceDAO.setPassword(ServiceRepositoryTest.FIRST_SERVICE_PASSWORD);
        serviceDAO.setRole(Role.READ);

        this.serviceRepository.save(serviceDAO);

        serviceDAO = new ServiceDAO();

        //CREATE OUR SECOND TEST USER AS AN ADMIN USER
        serviceDAO.setName(ServiceRepositoryTest.SECOND_SERVICE_NAME);
        serviceDAO.setPassword(ServiceRepositoryTest.SECOND_SERVICE_PASSWORD);
        serviceDAO.setRole(Role.FULL);

        this.serviceRepository.save(serviceDAO);
    }

    /**
     * This method tests that new services can be added successfully.
     */
    @Test
    public void testAddService() {
        ServiceDAO serviceDAO1 = new ServiceDAO();

        serviceDAO1.setName("saveTest");
        serviceDAO1.setPassword("savePassword");
        serviceDAO1.setRole(Role.READ);
        serviceDAO1.setCreationTime(ZonedDateTime.now().toEpochSecond());

        serviceDAO1 = this.serviceRepository.save(serviceDAO1);

        ServiceDAO serviceDAO2 = this.serviceRepository.findByName(serviceDAO1.getName());

        Assert.assertEquals(serviceDAO1.getId(), serviceDAO2.getId());
        Assert.assertEquals(serviceDAO1.getName(), serviceDAO2.getName());
        Assert.assertEquals(serviceDAO1.getPassword(), serviceDAO2.getPassword());
        Assert.assertEquals(serviceDAO1.getRole(), serviceDAO2.getRole());
        Assert.assertEquals(serviceDAO1.getCreationTime(), serviceDAO2.getCreationTime());
    }

    /**
     * This method tests that existing services can be updated successfully.
     */
    @Test
    public void testUpdateService() {
        ServiceDAO serviceDAO = this.serviceRepository.findByName(ServiceRepositoryTest.FIRST_SERVICE_NAME);

        Assert.assertNotNull(serviceDAO);
        Assert.assertEquals(serviceDAO.getRole(), Role.READ);

        serviceDAO.setName(ServiceRepositoryTest.UPDATE_SERVICE_NAME);
        serviceDAO.setPassword(ServiceRepositoryTest.UPDATE_SERVICE_PASSWORD);
        serviceDAO.setRole(Role.FULL);

        serviceDAO = this.serviceRepository.save(serviceDAO);

        ServiceDAO serviceDAO1 = this.serviceRepository.findByName(ServiceRepositoryTest.FIRST_SERVICE_NAME);
        Assert.assertNull(serviceDAO1);

        serviceDAO1 = this.serviceRepository.findByName(ServiceRepositoryTest.UPDATE_SERVICE_NAME);

        Assert.assertEquals(serviceDAO1.getId(), serviceDAO.getId());
        Assert.assertEquals(serviceDAO1.getName(), serviceDAO.getName());
        Assert.assertEquals(serviceDAO1.getPassword(), serviceDAO.getPassword());
        Assert.assertEquals(serviceDAO1.getRole(), serviceDAO.getRole());
        Assert.assertEquals(serviceDAO1.getCreationTime(), serviceDAO.getCreationTime());
    }

    /**
     * This method tests that services can be deleted successfully.
     */
    @Test
    public void testDeleteService() {
        Page<ServiceDAO> serviceDAOS = this.serviceRepository.findAll(Pageable.unpaged());

        ServiceDAO serviceDAO = this.serviceRepository.findByName(ServiceRepositoryTest.FIRST_SERVICE_NAME);

        Assert.assertNotNull(serviceDAO);

        this.serviceRepository.delete(serviceDAO);

        Page<ServiceDAO> newServiceDAOS = this.serviceRepository.findAll(Pageable.unpaged());

        serviceDAO = this.serviceRepository.findByName(ServiceRepositoryTest.FIRST_SERVICE_NAME);

        Assert.assertNull(serviceDAO);
        Assert.assertEquals(serviceDAOS.getTotalElements() - 1, newServiceDAOS.getTotalElements());
    }
}