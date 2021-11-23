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

import com.pcoetsee.settingservice.persistence.dao.SettingDAO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * This repository represents the methods used for CRUD operations performed on the `settings_service`.`settings` table.
 */
@Repository
public interface SettingRepository extends PagingAndSortingRepository<SettingDAO, Long> {
    /**
     * Fetches a setting, if it exists based on the supplied parameters.
     * <p>
     * If the supplied settings do not match an underlying record, then null is returned.
     *
     * @param serviceDAOName     the name of the service for which we are fetching the setting, null or empty returns null
     * @param serviceDAOPassword the password for the service for which we are fetching the setting, null or empty
     *                           returns null
     * @param name               the name of the setting we are looking for, null or empty returns null
     * @return the setting matching the supplied name belonging to the supplied service, if it exists, otherwise null
     */
    @Query("select s from SettingDAO s where s.serviceDAO.name = ?1 and s.serviceDAO.password = ?2 and s.name = ?3")
    SettingDAO findByServiceDAONameAndServiceDAOPasswordAndName(String serviceDAOName, String serviceDAOPassword, String name);

    /**
     * Fetches all settings that may exist based on the supplied parameters.
     * <p>
     * If no matches are found a null will be returned
     *
     * @param serviceDAOName     the name of the service for which we are fetching the setting, null or empty returns null
     * @param serviceDAOPassword the password for the service for which we are fetching the setting, null or empty
     *                           returns null
     * @param pageable           a {@link Pageable} object that allows for paging, null will return all results
     * @return a {@link Page} of settings name belonging to a matching service, if they exist, otherwise null
     */
    @Query("select s from SettingDAO s where s.serviceDAO.name = ?1 and s.serviceDAO.password = ?2")
    Page<SettingDAO> findAllByServiceServiceDAONameAndServiceDAOPassword(String serviceDAOName, String serviceDAOPassword, Pageable pageable);
}
