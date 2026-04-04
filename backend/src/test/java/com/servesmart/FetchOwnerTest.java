package com.servesmart;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.servesmart.repository.StaffRepository;
import com.servesmart.entity.Staff;
import com.servesmart.entity.StaffRole;
import java.util.List;

@SpringBootTest
public class FetchOwnerTest {

    @Autowired
    private StaffRepository staffRepository;

    @Test
    public void fetchOwnerFor54() {
        System.out.println("--- FETCHING OWNER FOR HOTEL 54 ---");
        List<Staff> staffList = staffRepository.findByRestaurantId(54L);
        for (Staff s : staffList) {
            if (s.getRole() == StaffRole.OWNER) {
                System.out.println("OWNER_FOUND: " + s.getUsername() + " | PasswordHash: " + s.getPassword());
            } else {
                System.out.println("STAFF_FOUND: " + s.getRole() + " | " + s.getUsername());
            }
        }
        System.out.println("--- END FETCHING ---");
    }
}
