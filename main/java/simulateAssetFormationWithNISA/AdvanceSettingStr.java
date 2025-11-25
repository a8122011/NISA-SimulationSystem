//2つの文字列フィールドを引数で受け取って初期化するデータ保持クラス

package java.simulateAssetFormationWithNISA;

public class advancedSettingStr { //公開クラス
    String requestAnnualChangeMoney;
    String requestEndingAge;

    public advancedSettingStr(String requestAnnualChangeMoney, String requestEndingAge) { //コンストラクタ インスタンス生成時に2つの文字列を受け取り、そのままフィールドに代入する
        this.requestAnnualChangeMoney = requestAnnualChangeMoney;
        this.requestEndingAge = requestEndingAge;
    }
}
