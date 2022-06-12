package io.swagger.IT;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources",
        glue = "io.swagger.IT.steps",
        plugin = "pretty"
)
public class CucumberIT {
}
