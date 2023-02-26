package al.silvio.warehouse.utils;

import org.apache.commons.text.RandomStringGenerator;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.apache.commons.text.CharacterPredicates.DIGITS;

@Component
public class PasswordUtils {
    
    public String getRandomPassword() {
        String oneTimePassword = generateRandomSpecialCharacters().concat(generateRandomNumbers())
                .concat(generateRandomAlphabet(true)).concat(generateRandomAlphabet(false));
        List<Character> characterList = oneTimePassword.chars().mapToObj(data -> (char) data)
                .collect(Collectors.toList());
        Collections.shuffle(characterList);
        return characterList.stream().collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }
    
    private String generateRandomSpecialCharacters() {
        RandomStringGenerator generator = new RandomStringGenerator.Builder().withinRange(33, 45).build();
        return generator.generate(2);
    }
    
    private String generateRandomNumbers() {
        RandomStringGenerator generator = new RandomStringGenerator.Builder().withinRange('0', 'z').filteredBy(DIGITS)
                .build();
        return generator.generate(2);
    }
    
    private String generateRandomAlphabet(boolean check) {
        RandomStringGenerator generator = check ?
                new RandomStringGenerator.Builder().withinRange('a', 'z').build() :
                new RandomStringGenerator.Builder().withinRange('A', 'Z').build();
        return generator.generate(2);
    }
}