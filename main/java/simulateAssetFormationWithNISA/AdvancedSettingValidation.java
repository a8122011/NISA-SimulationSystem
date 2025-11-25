package java.simulateAssetFormationWithNISA;

public class AdvancedSettingValidation {
    String annualChangeMoneyError;
    String endingAgeError; //エラーメッセージを保持するためのString

    public AdvancedSettingValidation() {
    }

    public void setAnnualChangeMoneyError(String annualChangeMoneyError) {
        this.annualChangeMoneyError = annualChangeMoneyError;
    }

    public void setEndingAgeError(String endingAgeError) {
        this.endingAgeError = endingAgeError;
    }
}
