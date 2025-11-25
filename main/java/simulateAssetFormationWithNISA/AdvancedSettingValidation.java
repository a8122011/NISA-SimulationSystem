package java.simulateAssetFormationWithNISA;

public class advancedSettingValidation {
    String annualChangeMoneyError;
    String endingAgeError; //エラーメッセージを保持するためのString

    public advancedSettingValidation() {
    }

    public void setAnnualChangeMoneyError(String annualChangeMoneyError) {
        this.annualChangeMoneyError = annualChangeMoneyError;
    }

    public void setEndingAgeError(String endingAgeError) {
        this.endingAgeError = endingAgeError;
    }
}
