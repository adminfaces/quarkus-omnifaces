package io.quarkus.omnifaces;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.net.URL;

import org.junit.jupiter.api.Test;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;

import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class QuarkusOmniFacesTest {

    @TestHTTPResource
    URL url;

    @Test
    public void shouldOpenIndexPage() throws Exception {
        try (final WebClient webClient = new WebClient(BrowserVersion.CHROME)) {
            webClient.getOptions().setJavaScriptEnabled(true);
            webClient.getOptions().setCssEnabled(false);
            webClient.getOptions().setUseInsecureSSL(true);
            webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
            webClient.getCookieManager().setCookiesEnabled(true);
            webClient.setAjaxController(new NicelyResynchronizingAjaxController());
            webClient.getCookieManager().setCookiesEnabled(true);

            final HtmlPage page = webClient.getPage(url + "/index.xhtml?id=1");

            assertThat(page.getTitleText())
                    .isEqualTo("Welcome to Quarkus OmniFaces extension!");
            assertThat(page.getElementById("id").asText())
                    .contains("1");
            assertThat(page.getElementById("viewAction").asText())
                    .startsWith("viewAction was called");
            assertThat(page.getElementById("preRenderView").asText())
                    .startsWith("preRenderView was called");

            final HtmlForm form = page.getFormByName("form");

            final HtmlButton button = (HtmlButton) form.getElementsByAttribute("button", "id", "btn-save").get(0);
            final HtmlPage result = button.click();
            assertThat(result.asXml())
                    .contains("Form saved!");

        }
    }
}
