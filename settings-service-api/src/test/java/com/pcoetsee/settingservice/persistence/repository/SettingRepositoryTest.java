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
import com.pcoetsee.settingservice.persistence.dao.SettingDAO;
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
import java.time.temporal.ChronoUnit;

/**
 * Class used for testing the repository accessing the `settings` table in the database.
 * <p>
 * Tests all general crud operations.
 */
@RunWith(SpringRunner.class)
@DataJpaTest()
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE, replace = AutoConfigureTestDatabase.Replace.NONE)
public class SettingRepositoryTest {

    /**
     * Name to be used for the service test settings will be linked to.
     */
    private static final String SERVICE_NAME = "firstService";
    /**
     * Password to be used for the service test settings will be linked to.
     */
    private static final String SERVICE_PASSWORD = "firstPassword";
    /**
     * Name to be used for the first setting.
     */
    private static final String FIRST_SETTING_NAME = "firstName";
    /**
     * Value to be used for the first setting.
     */
    private static final String FIRST_SETTING_VALUE = "firstValue";
    /**
     * Name to be used for the first setting.
     */
    private static final String SECOND_SETTING_NAME = "secondName";
    /**
     * Value to be used for the second setting.
     */
    private static final String SECOND_SETTING_VALUE = "secondValue";
    /**
     * Name to be used for the update setting.
     */
    private static final String UPDATE_SETTING_NAME = "updateName";
    /**
     * Value to be used for the update setting.
     */
    private static final String UPDATE_SETTING_VALUE = "updateValue";
    /**
     * ID to be used for the service test settings will be linked to.
     */
    private static Long SERVICE_ID = null;
    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private SettingRepository settingRepository;

    /**
     * This method sets up data for testing, one setting with last used set, and one without
     */
    @Before
    public void init() {
        ServiceDAO serviceDAO = new ServiceDAO();

        //Create a service to link settings to.
        serviceDAO.setName(SettingRepositoryTest.SERVICE_NAME);
        serviceDAO.setPassword(SettingRepositoryTest.SERVICE_PASSWORD);
        serviceDAO.setRole(Role.READ);

        serviceDAO = this.serviceRepository.save(serviceDAO);
        SettingRepositoryTest.SERVICE_ID = serviceDAO.getId();

        System.out.println("HERE IS YOUR STUPID ID! " + serviceDAO.getId());

        SettingDAO settingDAO = new SettingDAO();

        //Create a setting with a date last used.
        settingDAO.setServiceDAO(new ServiceDAO(SettingRepositoryTest.SERVICE_ID));
        settingDAO.setName(SettingRepositoryTest.FIRST_SETTING_NAME);
        settingDAO.setValue(SettingRepositoryTest.FIRST_SETTING_VALUE);
        settingDAO.setDateLastUsed(ZonedDateTime.now());

        this.settingRepository.save(settingDAO);

        settingDAO = new SettingDAO();

        //Create a setting with no date last used.
        settingDAO.setServiceDAO(new ServiceDAO(SettingRepositoryTest.SERVICE_ID));
        settingDAO.setName(SettingRepositoryTest.SECOND_SETTING_NAME);
        settingDAO.setValue(SettingRepositoryTest.SECOND_SETTING_VALUE);

        this.settingRepository.save(settingDAO);
    }

    /**
     * This method tests that new settings can be added successfully.
     */
    @Test
    public void testAddService() {
        SettingDAO settingDAO1 = new SettingDAO();

        settingDAO1.setName("saveTest");
        settingDAO1.setValue("saveValue");
        settingDAO1.setServiceDAO(new ServiceDAO(SettingRepositoryTest.SERVICE_ID));
        settingDAO1.setDateLastUsed(ZonedDateTime.now().minus(11, ChronoUnit.MONTHS));

        settingDAO1 = this.settingRepository.save(settingDAO1);

        SettingDAO settingDAO2 = this.settingRepository.findByServiceDAONameAndServiceDAOPasswordAndName(
                SettingRepositoryTest.SERVICE_NAME,
                SettingRepositoryTest.SERVICE_PASSWORD,
                "saveTest"
        );

        Assert.assertEquals(settingDAO1.getName(), settingDAO2.getName());
        Assert.assertEquals(settingDAO1.getValue(), settingDAO2.getValue());
        Assert.assertEquals(settingDAO1.getServiceDAO().getId(), settingDAO2.getServiceDAO().getId());
        Assert.assertEquals(settingDAO1.getDateLastUsed(), settingDAO2.getDateLastUsed());
    }

    /**
     * This method tests that existing services can be updated successfully.
     */
    @Test
    public void testUpdateService() {
        SettingDAO settingDAO = this.settingRepository.findByServiceDAONameAndServiceDAOPasswordAndName(
                SettingRepositoryTest.SERVICE_NAME,
                SettingRepositoryTest.SERVICE_PASSWORD,
                SettingRepositoryTest.FIRST_SETTING_NAME
        );

        Assert.assertNotNull(settingDAO);
        Assert.assertEquals(settingDAO.getValue(), SettingRepositoryTest.FIRST_SETTING_VALUE);

        settingDAO.setName(SettingRepositoryTest.UPDATE_SETTING_NAME);
        settingDAO.setValue(SettingRepositoryTest.UPDATE_SETTING_VALUE);
        settingDAO.setDateLastUsed(ZonedDateTime.now());

        this.settingRepository.save(settingDAO);

        SettingDAO settingDAO1 = this.settingRepository.findByServiceDAONameAndServiceDAOPasswordAndName(
                SettingRepositoryTest.SERVICE_NAME,
                SettingRepositoryTest.SERVICE_PASSWORD,
                SettingRepositoryTest.UPDATE_SETTING_NAME
        );

        Assert.assertNotNull(settingDAO1);
        Assert.assertEquals(settingDAO1.getId(), settingDAO.getId());
        Assert.assertEquals(settingDAO1.getName(), settingDAO.getName());
        Assert.assertEquals(settingDAO1.getServiceDAO(), settingDAO.getServiceDAO());
        Assert.assertEquals(settingDAO1.getDateLastUsed(), settingDAO.getDateLastUsed());
    }

    /**
     * This method tests that services can be deleted successfully.
     */
    @Test
    public void testDeleteService() {
        Page<SettingDAO> settingDAOS = this.settingRepository.findAllByServiceServiceDAONameAndServiceDAOPassword(
                SettingRepositoryTest.SERVICE_NAME,
                SettingRepositoryTest.SERVICE_PASSWORD,
                Pageable.unpaged()
        );

        SettingDAO settingDAO = this.settingRepository.findByServiceDAONameAndServiceDAOPasswordAndName(
                SettingRepositoryTest.SERVICE_NAME,
                SettingRepositoryTest.SERVICE_PASSWORD,
                SettingRepositoryTest.FIRST_SETTING_NAME
        );

        Assert.assertNotNull(settingDAO);

        this.settingRepository.delete(settingDAO);


        Page<SettingDAO> newSettingDAOS = this.settingRepository.findAllByServiceServiceDAONameAndServiceDAOPassword(
                SettingRepositoryTest.SERVICE_NAME,
                SettingRepositoryTest.SERVICE_PASSWORD,
                Pageable.unpaged()
        );

        settingDAO = this.settingRepository.findByServiceDAONameAndServiceDAOPasswordAndName(
                SettingRepositoryTest.SERVICE_NAME,
                SettingRepositoryTest.SERVICE_PASSWORD,
                SettingRepositoryTest.FIRST_SETTING_NAME
        );

        Assert.assertNull(settingDAO);
        Assert.assertEquals(settingDAOS.getTotalElements() - 1, newSettingDAOS.getTotalElements());
    }
}