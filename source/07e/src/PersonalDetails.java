import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.logging.Logger;

/**
 * The contents of a personal details file.
 * This class represents the data retrieved from the HRDatabase as it is passed within the system.
 * Changes made to this object will not be reflected in the HRDatabase unless amendPersonalDetails
 *  is explicitly called and authorised.
 * @author Marin md485
 * @version 20190301
 */
public class PersonalDetails {
    private LinkedHashMap<String, String> details;
    private final static Logger LOGGER = Logger.getLogger(AppController.class.getName());

    PersonalDetails() {
        details = new LinkedHashMap<>();

        details.put("Staff No", null);
        details.put("Surname", null);
        details.put("Name", null);
        details.put("Date of Birth", null);
        details.put("Address", null);
        details.put("Town/City", null);
        details.put("Post Code", null);
        details.put("Telephone Number", null);
        details.put("Mobile Number", null);
        details.put("Emergency Contact", null);
        details.put("Emergency Contact Number", null);
    }

    PersonalDetails(String staffNo, String surname, String name, String dob,
                    String address, String town, String postCode, String telephoneNo,
                    String mobileNo, String emergencyContact, String contactNo) {
        details = new LinkedHashMap<>();

        details.put("Staff No", staffNo);
        details.put("Surname", surname);
        details.put("Name", name);
        details.put("Date of Birth", dob);
        details.put("Address", address);
        details.put("Town/City", town);
        details.put("Post Code", postCode);
        details.put("Telephone Number", telephoneNo);
        details.put("Mobile Number", mobileNo);
        details.put("Emergency Contact", emergencyContact);
        details.put("Emergency Contact Number", contactNo);
    }

    LinkedHashMap<String, String> getAllDetails() {
        return details;
    }

    String printDetails() {  String result = "";
        for(Map.Entry<String, String> field : details.entrySet()) {
            result += field.getKey() + ": " + field.getValue() + "\n";
        }
        return result;
    }

    LinkedHashSet<String> returnFields() { return new LinkedHashSet<>(details.keySet()); }

    String getField(String field) {
        return details.get(field);
    }

    Boolean setField(String field, String entry) {
        return (entry.equals(details.put(field, entry)));
    }
}
