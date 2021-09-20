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

package com.pcoetsee.settingservice.dto;

import java.io.Serializable;
import java.time.ZonedDateTime;

/**
 * Data transfer object for sending information regarding services from one place to another.
 */
public class ServiceDTO implements Serializable {

    private static final long serialVersionUID = 8001563989116887460L;

    /**
     * The name of the service.
     */
    private String name;

    /**
     * Indicates whether the service is allowed to create new service records.
     */
    private boolean admin;

    /**
     * The date and time this service was created.
     */
    private ZonedDateTime creationTime;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public ZonedDateTime getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(ZonedDateTime creationTime) {
        this.creationTime = creationTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ServiceDTO)) return false;

        ServiceDTO that = (ServiceDTO) o;

        if (isAdmin() != that.isAdmin()) return false;
        if (!getName().equals(that.getName())) return false;
        return getCreationTime().equals(that.getCreationTime());
    }

    @Override
    public int hashCode() {
        int result = getName().hashCode();
        result = 31 * result + (isAdmin() ? 1 : 0);
        result = 31 * result + getCreationTime().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ServiceDTO{" +
                "name='" + name + '\'' +
                ", admin=" + admin +
                ", creationTime=" + creationTime +
                '}';
    }
}
