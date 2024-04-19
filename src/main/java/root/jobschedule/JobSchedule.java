package root.jobschedule;

import root.entity.User;
import root.repository.UserRepo;
import root.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Component
public class JobSchedule {

    private final UserRepo userRepo;
    private final EmailService emailService;

    /**
     * see cron schedule expressions: https://crontab.guru/
     * 00 01 09 * * *
     * seconds minus hour day month monday-sunday
     * * : any value
     * , : value list separator
     * - : range of values
     * / : step values
     */
    /*@Scheduled(cron = "00 01 09 * * *")
    public void goodMorningAt09amEveryDay() {
        log.warn("Good morning !");
    }*/

    // test
    /*@Scheduled(cron = "00 21 09 * * *")
    public void testCronEmail() {
        log.warn("root.jobschedule.testCronEmail()");
        emailService.testEmail();
    }*/
    @Scheduled(cron = "00 00 09 * * *")
    public void cronSendBirthdayEmail() {
        log.warn("JobSchedule.cronSendBirthdayEmail()");

        Calendar calendar = Calendar.getInstance();
        int date = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH) + 1; // month start with 0

        List<User> userList = userRepo.searchByBirthdate(date, month);

        for (User user : userList) {
            log.warn("Happy Birthday " + user.getName());
            emailService.sendBirthdayEmail(user.getEmail(), user.getName());
        }
    }

    // test
    /*@Scheduled(cron = "30 23 21 * * *")
    public void happyBirthday() {

        Calendar calendar = Calendar.getInstance();
        int date = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH) + 1; // month start with 0

        List<User> userList = userRepo.searchByBirthdate(date, month);

        for (User user : userList) {
            log.warn("Happy Birthday " + user.getName());
        }
    }*/
}
