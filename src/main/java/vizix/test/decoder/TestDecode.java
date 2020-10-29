package vizix.test.decoder;

public class TestDecode {
    public static void main(String[] args){
        String[] epcList = {
                "FE047DC7EC134306584711A118331EC5",
                "FE001A1A658B09079847CD43E0600000"/*
                "FE24A5A4FA49530A7DEDE6C3E0AD0000",
                "FE24A5A52B0AF30A85EDE6C5EC5F0000",
                "FE24A5A4FA026B0B0CF6D9F3CE160000",
                "FE031045FAAC0F0B7E16E080CCF60000",
                "FE24A59A7BE45B0C0CF8D3B3E6EE0000",
                "FE031045FF4FD70C0E16ED2FC0EF0000",
                "FE02579D45B783052D97C695F2770000",
                "FE26ED220BC227075E3EED03D9C80000",
                "FE24A5A4F9FEBB0A5616C196F2560000",
                "FE24A5A4F9FED70A54F6DA1CE6720000",
                "FE031BC79DE071091E17F38EC04F0000",
                "FE0338A61DAEF9093CF9E0AFF36C0000",
                "FE031BC79DDF1D090E3FCCFBE0310000",
                "FE2757DE7840B5091615CCF6D9F30000"*/};
        DecodeGTIN128 decodeGTIN128 = new DecodeGTIN128();
        //String inputEPC = "FE001612881BAF0A7DEDF9D7F3B60000";
        for(String inputEPC : epcList){
            decodeGTIN128.bnDecodeClick(inputEPC);
            System.out.println("***** Results for EPC: "+inputEPC );
            System.out.println(decodeGTIN128.toResponseString());
            //System.out.println(decodeGTIN128.getCx128In_Text());
        }

    }
}

