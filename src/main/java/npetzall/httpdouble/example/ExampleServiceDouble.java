package npetzall.httpdouble.example;

import npetzall.httpdouble.api.Request;
import npetzall.httpdouble.api.Response;
import npetzall.httpdouble.api.ServiceDouble;
import npetzall.httpdouble.api.ServiceDoubleConfiguration;
import npetzall.xpath.simple.api.XPathProcessorFactory;

import java.util.Map;

public class ExampleServiceDouble implements ServiceDouble {

    private static final String MIME_XML = "text/xml";

    private static final String REVERSE_TEXT = "reverseText";
    private static final String TO_UPPERCASE = "toUpperCase";
    private static final String GET_QUOTATION = "getQuotation";

    private final XPathProcessorFactory xPathProcessorFactory;

    public ExampleServiceDouble() {
        xPathProcessorFactory = XPathProcessorFactory.builder()
                .addPrefixAndNamespace("s","http://www.w3.org/2001/12/soap-envelope")
                .addPrefixAndNamespace("q","http://npetzall.http-double/example/getQuotation")
                .addPrefixAndNamespace("r","http://npetzall.http-double/example/reverseText")
                .addPrefixAndNamespace("u","http://npetzall.http-double/example/toUpperCase")
                .addSetOnMatch("/s:Envelope/s:Body/q:GetQuotationRequest", "service", GET_QUOTATION)
                .addSetOnMatch("/s:Envelope/s:Body/r:ReverseTextRequest", "service",REVERSE_TEXT)
                .addSetOnMatch("/s:Envelope/s:Body/u:ToUpperCaseRequest", "service",TO_UPPERCASE)
                .addExtractTextFirst("/s:Envelope/s:Body/q:GetQuotationRequest/q:QuotationsName",GET_QUOTATION)
                .addExtractTextFirst("/s:Envelope/s:Body/r:ReverseTextRequest/r:text",REVERSE_TEXT)
                .addExtractTextFirst("/s:Envelope/s:Body/u:ToUpperCaseRequest/u:text",TO_UPPERCASE)
                .build();
    }

    @Override
    public void configure(ServiceDoubleConfiguration serviceDoubleConfiguration) {
        serviceDoubleConfiguration
            .name("Example")
            .urlPath("/example")
            .addTemplate(REVERSE_TEXT, this.getClass().getResourceAsStream("/templates/reverseTextResponse.xml"))
            .addTemplate(TO_UPPERCASE, this.getClass().getResourceAsStream("/templates/toUpperCaseResponse.xml"))
            .addTemplate(GET_QUOTATION, this.getClass().getResourceAsStream("/templates/getQuotationResponse.xml"));
    }

    @Override
    public void processRequest(Request request, Response response) {
        Map<String,String> data = xPathProcessorFactory.newProcessor().process(request.body());
        String service = data.get("service");
        if (GET_QUOTATION.equals(service)) {
            getQuotation(response,data);
        } else if (REVERSE_TEXT.equals(service)) {
            reverseText(response, data);
        } else if (TO_UPPERCASE.equals(service)) {
            toUpperCase(response, data);
        }

    }

    private void getQuotation(Response response, Map<String,String> tokens) {
        response
                .templateName(GET_QUOTATION)
                .contentType(MIME_XML)
                .addTokens(tokens)
                .delay(5000,7000);
    }

    private void reverseText(Response response, Map<String,String> tokens) {
        response
                .templateName(REVERSE_TEXT)
                .contentType(MIME_XML)
                .addToken("text", new StringBuffer(tokens.get(REVERSE_TEXT)).reverse().toString())
                .delay(5000, 7000);
    }

    private void toUpperCase(Response response, Map<String,String> tokens) {
        response
                .templateName(TO_UPPERCASE)
                .contentType(MIME_XML)
                .addToken("text", tokens.get(TO_UPPERCASE).toUpperCase())
                .delay(5000, 7000);
    }
}
