package com.haeungun.mocketl.model;

import com.haeungun.mocketl.ApplicationConfig;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

public class UserLog {

    private final JSONObject json;

    private final int id;
    private final int payment;
    private final Optional<Object> age;
    private final Optional<Object> firstName;
    private final Optional<Object> lastName;
    private final Optional<Object> email;
    private final Optional<Object> gender;
    private final Optional<Object> ipAddress;
    private final Optional<Object> userAgent;
    private final Optional<Object> country;

    private final int defaultAge;
    private final String defaultCountry;

    public UserLog(JSONObject json, ApplicationConfig config) {
        this.json = json;
        this.id = json.getInt("id");
        this.payment = json.getInt("payment");

        this.age = Optional.ofNullable(json.get("age"));
        this.firstName = Optional.ofNullable(json.get("first_name"));
        this.lastName = Optional.ofNullable(json.get("last_name"));
        this.email = Optional.ofNullable(json.get("email"));
        this.gender = Optional.ofNullable(json.get("gender"));
        this.ipAddress = Optional.ofNullable(json.get("ip_address"));
        this.userAgent = Optional.ofNullable(json.get("user_agent"));
        this.country = Optional.ofNullable(json.get("country"));

        this.defaultAge = config.getDefaultUserAge();
        this.defaultCountry = config.getDefaultUserCountry();
    }

    public JSONObject getJson() {
        return this.json;
    }

    public int getId() {
        return this.id;
    }

    public String getFirstName() {
        return this.firstName.orElse("").toString();
    }

    public String getLastName() {
        return this.lastName.orElse("").toString();
    }

    public String getEmail() {
        return this.email.orElse("").toString();
    }

    public String getIpAddress() {
        return this.ipAddress.orElse("").toString();
    }

    public String getCountry() {
        return this.country.orElse(this.defaultCountry).toString();
    }

    public String getUserAgent() {
        return this.userAgent.orElse("").toString();
    }

    public String getGender() {
        return this.gender.orElse("").toString();
    }

    public int getAge() {
        return (int) this.age.orElse(this.defaultAge);
    }

    public int getPayment() {
        return this.payment;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof UserLog)) return false;

        JSONObject comparedJson = ((UserLog) obj).getJson();

        Set<String> keys = this.json.keySet();
        Set<String> comparedKeys = comparedJson.keySet();

        if (keys.size() != comparedKeys.size()) return false;

        Iterator<String> iterator = keys.iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            if (!comparedJson.has(key)) return false;

            Optional<Object> value = Optional.ofNullable(this.json.get(key));
            Optional<Object> comparedValue = Optional.ofNullable(comparedJson.get(key));

            if (!value.equals(comparedValue)) return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "UserLog[" + this.json.toString() + "]";
    }
}
