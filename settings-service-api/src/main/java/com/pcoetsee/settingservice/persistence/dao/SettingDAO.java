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

package com.pcoetsee.settingservice.persistence.dao;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;

/**
 * This Entity class represents the `settings_service`.`settings` table in the database.
 * <p>
 * This table stores the settings for a specific service.
 */
@Entity
@Table(name = "`settings`")
public class SettingDAO implements Serializable {

    private static final long serialVersionUID = 191468327370598534L;

    /**
     * The auto generated ID used in the table.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`id`")
    private Long id;

    /**
     * The ID of the service this setting belongs to.
     */
    @ManyToOne(fetch = FetchType.LAZY, targetEntity = ServiceDAO.class, optional = false)
    @JoinColumn(name = "`service_id`", referencedColumnName = "`id`")
    private ServiceDAO serviceDAO;

    /**
     * The unique name of the specific setting.
     */
    @Column(name = "`name`")
    private String name;

    /**
     * The value of the specific setting.
     */
    @Column(name = "`value`")
    private String value;

    /**
     * The date this setting was last requested.
     */
    @Column(name = "`date_last_used`")
    private ZonedDateTime dateLastUsed;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ServiceDAO getServiceDAO() {
        return serviceDAO;
    }

    public void setServiceDAO(ServiceDAO serviceDAO) {
        this.serviceDAO = serviceDAO;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public ZonedDateTime getDateLastUsed() {
        return dateLastUsed;
    }

    public void setDateLastUsed(ZonedDateTime dateLastUsed) {
        this.dateLastUsed = dateLastUsed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SettingDAO)) return false;

        SettingDAO that = (SettingDAO) o;

        if (!getId().equals(that.getId())) return false;
        if (!getServiceDAO().equals(that.getServiceDAO())) return false;
        if (!getName().equals(that.getName())) return false;
        if (!getValue().equals(that.getValue())) return false;
        return getDateLastUsed().equals(that.getDateLastUsed());
    }

    @Override
    public int hashCode() {
        int result = getId().hashCode();
        result = 31 * result + getServiceDAO().hashCode();
        result = 31 * result + getName().hashCode();
        result = 31 * result + getValue().hashCode();
        result = 31 * result + getDateLastUsed().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "SettingDAO{" +
                "id=" + id +
                ", serviceDAO=" + serviceDAO +
                ", name='" + name + '\'' +
                ", value='" + value + '\'' +
                ", dateLastUsed=" + dateLastUsed +
                '}';
    }
}
