package jp.java.simulator.simulateAssetFormationWithNISA;

public class AdvancedSettingValidation {
    String annualChangeMoneyError;
    String endingAgeError; //エラーメッセージを保持するためのString

    public AdvancedSettingValidation() {
    }

    public void setAnnualChangeMoneyError(String annualChangeMoneyError) { //void戻り値なしを意味する
        this.annualChangeMoneyError = annualChangeMoneyError;
    }

    public void setEndingAgeError(String endingAgeError) {
        this.endingAgeError = endingAgeError;
    }
}
