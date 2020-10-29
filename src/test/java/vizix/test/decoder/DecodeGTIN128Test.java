package vizix.test.decoder;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Assert;
import org.junit.Test;

import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

public class DecodeGTIN128Test {
    public static HashMap<String, String> lstEPC = new HashMap<>();


    static {
        lstEPC.put("FE001612881BAF0A7DEDF9D7F3B60000", "0100023700047595172010151077989890");
        lstEPC.put("FE24A5A4FA49530A7DEDE6C3E0AD0000", "0110073464017492172010151077665577");
        lstEPC.put("FE24A5A52B0AF30A85EDE6C5EC5F0000", "0110073464816316172010161077667721");
        lstEPC.put("FE24A5A4FA026B0B0CF6D9F3CE160000", "0110073464012954172011011010445288");
        lstEPC.put("FE031045FAAC0F0B7E16E080CCF60000", "0100842107104003172011151088562210");
        lstEPC.put("FE24A59A7BE45B0C0CF8D3B3E6EE0000", "0110073420003606172012011012345678");
        lstEPC.put("FE031045FF4FD70C0E16ED2FC0EF0000", "0100842107180021172012011088779041");
        lstEPC.put("FE02579D45B783052D97C695F2770000", "0100643831000544172005051051107811");
        lstEPC.put("FE26ED220BC227075E3EED03D9C80000", "0110699980075145172007111098765432");
        lstEPC.put("FE24A5A4F9FEBB0A5616C196F2560000", "0110073464012718172010101088088808");
        lstEPC.put("FE24A5A4F9FED70A54F6DA1CE6720000", "0110073464012725172010101010456644");
        lstEPC.put("FE031BC79DE071091E17F38EC04F0000", "0100854462003228132009031089880001");
        lstEPC.put("FE0338A61DAEF9093CF9E0AFF36C0000", "0100885460003774132009071013579876");
        lstEPC.put("FE031BC79DDF1D090E3FCCFBE0310000", "0100854462003143132009011099215543");
        lstEPC.put("FE2757DE7840B5091615CCF6D9F30000", "0110814587015213132009021087210445");


    }

    @Test
    public void testEPC() {
        lstEPC.forEach((k,v)-> {
            String EPC = k;
            DecodeGTIN128 decode = new DecodeGTIN128();
            decode.bnDecodeClick(EPC);
            Assert.assertEquals("Error for "+ k, v, decode.getCx128In_Text());
        });
    }

    @Test
    public void testEPC1() throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(new FileReader("/home/rchirinos/devMojix/ViZix/DecoderTest/src/main/resources/vizix.decode/DecodeExamples.json"));
        JSONObject jsonObject = (JSONObject) obj;
        JSONArray results = (JSONArray) jsonObject.get("results");
        results.stream().forEach(epc -> {
            JSONObject singleObject = (JSONObject) epc;
            String EPC = singleObject.get("EPC").toString();
            DecodeGTIN128 decode = new DecodeGTIN128();
            decode.bnDecodeClick(EPC);
            //System.out.println(EPC);
            Assert.assertEquals("Error in GTIN for EPC: "+EPC,
                    singleObject.get("GTIN"),decode.getCxGTIN_Text());
            Assert.assertEquals("Error in DateType for EPC: "+EPC,
                    singleObject.get("DateType").toString(), decode.getCxDateType_Text().get(decode.getCxDateType_SelectedIndex()));
            Assert.assertEquals("Error in Date for EPC: "+EPC,
                    singleObject.get("Date").toString(), decode.getCxDate_Text());
            Assert.assertEquals("Error in FriendlyDate for EPC: "+EPC,
                    singleObject.get("FriendlyDate").toString(), decode.getFriendlyDate());
            Assert.assertEquals("Error in LotCode for EPC: "+EPC,
                    singleObject.get("LotCode").toString(), decode.getCxLotNum_Text());
            Assert.assertEquals("Error in GTIN-128 for EPC: "+EPC,
                    singleObject.get("GTIN-128").toString(), decode.getCx128In_Text());

        });
    }

    @Test
    public void testEPC2() throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(new FileReader("/resources/vizix/decode/DecodeExamplesError.json"));
        JSONObject jsonObject = (JSONObject) obj;
        JSONArray results = (JSONArray) jsonObject.get("results");
        results.stream().forEach(epc -> {
            JSONObject singleObject = (JSONObject) epc;
            String EPC = singleObject.get("EPC").toString();
            DecodeGTIN128 decode = new DecodeGTIN128();
            decode.bnDecodeClick(EPC);
            //System.out.println(EPC);
            Assert.assertEquals("Error in GTIN for EPC: "+EPC,
                    singleObject.get("GTIN"),decode.getCxGTIN_Text());
            Assert.assertEquals("Error in DateType for EPC: "+EPC,
                    singleObject.get("DateType"), decode.getCxDateType_Text().get(decode.getCxDateType_SelectedIndex()));
            Assert.assertEquals("Error in Date for EPC: "+EPC,
                    singleObject.get("Date"), decode.getCxDate_Text());
            Assert.assertEquals("Error in FriendlyDate for EPC: "+EPC,
                    singleObject.get("FriendlyDate"), decode.getFriendlyDate());
            Assert.assertEquals("Error in LotCode for EPC: "+EPC,
                    singleObject.get("LotCode"), decode.getCxLotNum_Text());
            Assert.assertEquals("Error in GTIN-128 for EPC: "+EPC,
                    singleObject.get("GTIN-128"), decode.getCx128In_Text());


        });
    }

    @Test
    public void testEPC3() throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(new FileReader("/resources/vizix/decode/DecodeExamplesError3.json"));
        JSONObject jsonObject = (JSONObject) obj;
        JSONArray results = (JSONArray) jsonObject.get("results");
        results.stream().forEach(epc -> {
            JSONObject singleObject = (JSONObject) epc;
            String EPC = singleObject.get("EPC").toString();
            DecodeGTIN128 decode = new DecodeGTIN128();
            decode.bnDecodeClick(EPC);
            //System.out.println(EPC);
            Assert.assertEquals("Error in GTIN for EPC: "+EPC,
                    singleObject.get("GTIN"),decode.getCxGTIN_Text());
            Assert.assertEquals("Error in DateType for EPC: "+EPC,
                    singleObject.get("DateType"), decode.getCxDateType_Text().get(decode.getCxDateType_SelectedIndex()));
            Assert.assertEquals("Error in Date for EPC: "+EPC,
                    singleObject.get("Date"), decode.getCxDate_Text());
            Assert.assertEquals("Error in FriendlyDate for EPC: "+EPC,
                    singleObject.get("FriendlyDate"), decode.getFriendlyDate());
            Assert.assertEquals("Error in LotCode for EPC: "+EPC,
                    singleObject.get("LotCode"), decode.getCxLotNum_Text());
            Assert.assertEquals("Error in GTIN-128 for EPC: "+EPC,
                    singleObject.get("GTIN-128"), decode.getCx128In_Text());


        });
    }
}
