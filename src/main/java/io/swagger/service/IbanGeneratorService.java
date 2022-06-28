package io.swagger.service;

import io.swagger.repo.AccountRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ObjectInputFilter;
import java.util.Objects;
import java.util.Random;

@Service
public class IbanGeneratorService
{
    private AccountRepo accountRepo;

    // General format that needs to be created : NLxxINHO0xxxxxxxxx
    public static final String BANKSIBAN = "NL01INHO0000000001";
    private final String PREFIX = "NL";
    private final String PREFIX_INH = "INHO0";


    public String generateIban()
    {
        String newIban;
        do
        {
            int twoDigit = getRandomInt(1, 99);
            int nineDigit = getRandomInt(1, 999999999);

            newIban = formIban(twoDigit, nineDigit);

        } while (ibanIsEligible(newIban));
        System.out.println(newIban);
        return newIban;
    }

    private String formIban(int twoDigit, int nineDigit)
    {
        return String.format("%s%02d%s%09d", this.PREFIX, twoDigit, this.PREFIX_INH, nineDigit);
    }

    private boolean ibanIsEligible(String iban)
    {
        return Objects.isNull(accountRepo.findByIban(iban)) &&
                !iban.equals(BANKSIBAN);
    }

    //For randomizing the number parts of the generated IBAN.
    private int getRandomInt(int min, int max)
    {
        return new Random().nextInt(max + 1 - min) + min;
    }
}
