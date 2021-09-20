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

import com.pcoetsee.settingservice.persistence.dao.ServiceDAO;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * This repository represents the methods used for CRUD operations performed on the `settings_service`.`services` table.
 */
@Repository
public interface ServiceRepository extends CrudRepository<ServiceDAO, Long> {
    /**
     * This method finds a specific service by matching a name and a password.
     *
     * @param name     the name of the service we're looking for, null or empty returns null
     * @param password the password of the service we are looking for, null or empty returns null
     * @return a service, but only if the supplied name and password find a match in the database, otherwise null
     */
    ServiceDAO getServiceDAOByNameAndPassword(String name, String password);
}
