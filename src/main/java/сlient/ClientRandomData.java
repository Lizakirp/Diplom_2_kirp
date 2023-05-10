package сlient;

import org.apache.commons.lang3.RandomStringUtils;

public class ClientRandomData extends Client {
    public Client getRandomUser() {
        String email = RandomStringUtils.randomAlphabetic(9) + "@mail.ru";
        String password = RandomStringUtils.randomAlphabetic(9);
        String name = RandomStringUtils.randomAlphabetic(9);
        return new Client(email, password, name);
    }
}
