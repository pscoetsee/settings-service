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

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collection;

/**
 * This entity class represent the `settings_service`.`services` table in the database.
 * <p>
 * This table stores services that are allowed to connect to this server, along with some other details.
 */
@Entity
@Table(name = "`services`")
public class ServiceDAO implements UserDetails {

    /**
     * The serial version UID for the class.
     */
    private static final long serialVersionUID = -8522180443390975944L;

    /**
     * The autogenerated ID used in the table.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`id`")
    private Long id;

    /**
     * A descriptive name for the service.
     */
    @Column(name = "`name`")
    private String name;

    /**
     * The password that should be used by the service to connect to this application.
     * <p>
     * We should only store passwords that have been hashed.
     */
    @Column(name = "`password`")
    private String password;

    /**
     * Indicates whether the service represented by this record can create new users.
     */
    @Column(name = "`role`")
    private Role role;

    /**
     * A long representing the time and date that the record was created.
     */
    @Column(name = "`creation_time`")
    private Long creationTime;

    /**
     * Default constructor will set the creation time.
     */
    public ServiceDAO() {
        this.creationTime = ZonedDateTime.now().toEpochSecond();
    }

    /**
     * Constructor that sets only the ID, to be used when searching or updating {@link SettingDAO} objects.
     *
     * @param id the ID of a service object, null may cause undesired effects.
     */
    public ServiceDAO(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays.asList(this.role);
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return this.name;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Long creationTime) {
        this.creationTime = creationTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ServiceDAO)) return false;

        ServiceDAO that = (ServiceDAO) o;

        if (getRole() != that.getRole()) return false;
        if (!getId().equals(that.getId())) return false;
        if (!getName().equals(that.getName())) return false;
        if (!getPassword().equals(that.getPassword())) return false;
        return getCreationTime().equals(that.getCreationTime());
    }

    @Override
    public int hashCode() {
        int result = getId().hashCode();
        result = 31 * result + getName().hashCode();
        result = 31 * result + getPassword().hashCode();
        result = 31 * result + (getRole().hashCode());
        result = 31 * result + getCreationTime().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ServiceDAO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", admin=" + role +
                ", creationTime=" + creationTime +
                '}';
    }
}
