package jp.java.simulator.simulateAssetFormationWithNISA;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.util.*;

@Controller
public class HomeController {
    // record 定義
    record LifeEventParams(String lifeEvent1, int lifeEventAge1, double requiredFunds1,
                           String lifeEvent2, int lifeEventAge2, double requiredFunds2,
                           String lifeEvent3, int lifeEventAge3, double requiredFunds3,
                           String lifeEvent4, int lifeEventAge4, double requiredFunds4,
                           String lifeEvent5, int lifeEventAge5, double requiredFunds5) {}

    record AdvancedSetting(int annualChangeMonth, int annualChangeMoney, int endingAge) {}

    record SimulationParams(String id, double expectedRateOfReturn, double volatility,
                            int startAge, double monthlySavings, double initialValue,
                            LifeEventParams lifeEventParams, AdvancedSetting advancedSetting) {}
    private double expectedRateOfReturn;
    private double volatility;
    private int startAge;
    private double monthlySavings;
    private double initialValue;
    private LifeEventParams lifeEventParams;
    private AdvancedSetting advancedSetting;
    private SimulationParams params;
    private lifeEventValidation lifeEventValidMessage = new lifeEventValidation();
    private advancedSettingValidation advancedSettingValidMessage = new advancedSettingValidation();
    private Validation validMessage = new Validation();
    boolean validateFlg = false;
    List<List<Double>> valuationData;
    List<String> countList;
    double suggestedMax;
    int stepSize;

    @GetMapping("/list")
    String listItems(Model model) {
        if (params != null && params.id() != null) {
            if (validateFlg) {
                model.addAttribute("params", params);
                int i = 0;
                for (List<Double> data : valuationData) {
                    switch (i) {
                        case 0 -> model.addAttribute("top30Percent", data);
                        case 1 -> model.addAttribute("median", data);
                        case 2 -> model.addAttribute("bottom30Percent", data);
                        case 3 -> model.addAttribute("bottom10Percent", data);
                        case 4 -> model.addAttribute("noOperation", data);
                    }
                    i++;
                }
                model.addAttribute("monthCountList", countList);
                model.addAttribute("suggestedMax", suggestedMax);
                model.addAttribute("stepSize", stepSize);
            } else {
                model.addAttribute("params", params);
                valuationData = Simulation.getValuationData(params);
                int i = 0;
                for (List<Double> data : valuationData) {
                    switch (i) {
                        case 0 -> model.addAttribute("top30Percent", data);
                        case 1 -> model.addAttribute("median", data);
                        case 2 -> model.addAttribute("bottom30Percent", data);
                        case 3 -> model.addAttribute("bottom10Percent", data);
                        case 4 -> model.addAttribute("noOperation", data);
                    }
                    i++;
                }
                countList = Simulation.getAgeCountList(params);
                model.addAttribute("monthCountList", countList);
                suggestedMax = Simulation.getSuggestedMax(valuationData);
                model.addAttribute("suggestedMax", suggestedMax);
                stepSize = Simulation.getStepSize(suggestedMax);
                model.addAttribute("stepSize", stepSize);
            }
        }
        model.addAttribute("expectedRateOfReturnError", validMessage.expectedRateOfReturnError);
        model.addAttribute("volatilityError", validMessage.volatilityError);
        model.addAttribute("startAgeError", validMessage.startAgeError);
        model.addAttribute("monthlySavingsError", validMessage.monthlySavingsError);
        model.addAttribute("initialValueError", validMessage.initialValueError);
        model.addAttribute("lifeEventAge1Error", lifeEventValidMessage.lifeEventAge1Error);
        model.addAttribute("requiredFunds1Error", lifeEventValidMessage.requiredFunds1Error);
        model.addAttribute("lifeEventAge2Error", lifeEventValidMessage.lifeEventAge2Error);
        model.addAttribute("requiredFunds2Error", lifeEventValidMessage.requiredFunds2Error);
        model.addAttribute("lifeEventAge3Error", lifeEventValidMessage.lifeEventAge3Error);
        model.addAttribute("requiredFunds3Error", lifeEventValidMessage.requiredFunds3Error);
        model.addAttribute("lifeEventAge4Error", lifeEventValidMessage.lifeEventAge4Error);
        model.addAttribute("requiredFunds4Error", lifeEventValidMessage.requiredFunds4Error);
        model.addAttribute("lifeEventAge5Error", lifeEventValidMessage.lifeEventAge5Error);
        model.addAttribute("requiredFunds5Error", lifeEventValidMessage.requiredFunds5Error);
        model.addAttribute("annualChangeMoneyError", advancedSettingValidMessage.annualChangeMoneyError);
        model.addAttribute("endingAgeError", advancedSettingValidMessage.endingAgeError);

        return "mainpage";
    }

    @GetMapping("/add")
    public String addItem(
            @RequestParam("expectedRateOfReturn") String requestExpectedRateOfReturn,
            @RequestParam("volatility") String requestVolatility,
            @RequestParam("startAge") String requestStartAge,
            @RequestParam("monthlySavings") String requestMonthlySavings,
            @RequestParam("initialValue") String requestInitialValue,

            @RequestParam(value = "lifeEvent1", required = false, defaultValue = "") String lifeEvent1,
            @RequestParam(value = "lifeage1", required = false, defaultValue = "") String requestLifeEventAge1,
            @RequestParam(value = "requiredFunds1", required = false, defaultValue = "") String requestRequiredFunds1,

            @RequestParam(value = "lifeEvent2", required = false, defaultValue = "") String lifeEvent2,
            @RequestParam(value = "lifeage2", required = false, defaultValue = "") String requestLifeEventAge2,
            @RequestParam(value = "requiredFunds2", required = false, defaultValue = "") String requestRequiredFunds2,

            @RequestParam(value = "lifeEvent3", required = false, defaultValue = "") String lifeEvent3,
            @RequestParam(value = "lifeage3", required = false, defaultValue = "") String requestLifeEventAge3,
            @RequestParam(value = "requiredFunds3", required = false, defaultValue = "") String requestRequiredFunds3,

            @RequestParam(value = "lifeEvent4", required = false, defaultValue = "") String lifeEvent4,
            @RequestParam(value = "lifeage4", required = false, defaultValue = "") String requestLifeEventAge4,
            @RequestParam(value = "requiredFunds4", required = false, defaultValue = "") String requestRequiredFunds4,

            @RequestParam(value = "lifeEvent5", required = false, defaultValue = "") String lifeEvent5,
            @RequestParam(value = "lifeage5", required = false, defaultValue = "") String requestLifeEventAge5,
            @RequestParam(value = "requiredFunds5", required = false, defaultValue = "") String requestRequiredFunds5,

            @RequestParam(value = "annualChangePeriod", required = false, defaultValue = "") String annualChangePeriod,
            @RequestParam(value = "annualChangeMoney", required = false, defaultValue = "") String requestAnnualChangeMoney,
            @RequestParam(value = "endingAge", required = false, defaultValue = "") String requestEndingAge){
        String id = UUID.randomUUID().toString().substring(0, 8);
        try {
            // 必須項目の変換
            double expectedRateOfReturn = Double.parseDouble(requestExpectedRateOfReturn);
            double volatility = Double.parseDouble(requestVolatility);
            int startAge = Integer.parseInt(requestStartAge);
            double monthlySavings = Double.parseDouble(requestMonthlySavings);
            double initialValue = Double.parseDouble(requestInitialValue);

            // ライフイベント項目
            int lifeEventAge1 = 0; double requiredFunds1 = 0;
            int lifeEventAge2 = 0; double requiredFunds2 = 0;
            int lifeEventAge3 = 0; double requiredFunds3 = 0;
            int lifeEventAge4 = 0; double requiredFunds4 = 0;
            int lifeEventAge5 = 0; double requiredFunds5 = 0;
            int annualChangeMonth = 0; int annualChangeMoney = 0;
            int endingAge = 0;

            if (!requestLifeEventAge1.equals("") && !requestRequiredFunds1.equals("")) {
                lifeEventAge1 = Integer.parseInt(requestLifeEventAge1);
                requiredFunds1 = Double.parseDouble(requestRequiredFunds1);
            }
            if (!requestLifeEventAge2.equals("") && !requestRequiredFunds2.equals("")) {
                lifeEventAge2 = Integer.parseInt(requestLifeEventAge2);
                requiredFunds2 = Double.parseDouble(requestRequiredFunds2);
            }
            if (!requestLifeEventAge3.equals("") && !requestRequiredFunds3.equals("")) {
                lifeEventAge3 = Integer.parseInt(requestLifeEventAge3);
                requiredFunds3 = Double.parseDouble(requestRequiredFunds3);
            }
            if (!requestLifeEventAge4.equals("") && !requestRequiredFunds4.equals("")) {
                lifeEventAge4 = Integer.parseInt(requestLifeEventAge4);
                requiredFunds4 = Double.parseDouble(requestRequiredFunds4);
            }
            if (!requestLifeEventAge5.equals("") && !requestRequiredFunds5.equals("")) {
                lifeEventAge5 = Integer.parseInt(requestLifeEventAge5);
                requiredFunds5 = Double.parseDouble(requestRequiredFunds5);
            }
            
            // 詳細設定
            if (!annualChangePeriod.isBlank() && !requestAnnualChangeMoney.isBlank()) {
                annualChangeMonth = getAnnualChangeMonth(annualChangePeriod);
                annualChangeMoney = Integer.parseInt(requestAnnualChangeMoney);
            }
            if (!requestEndingAge.isBlank()) {
                endingAge = Integer.parseInt(requestEndingAge);
            }

            // 値のセット
            lifeEventParams = new LifeEventParams(lifeEvent1, lifeEventAge1, requiredFunds1, lifeEvent2, lifeEventAge2, requiredFunds2, lifeEvent3, lifeEventAge3, requiredFunds3, lifeEvent4, lifeEventAge4, requiredFunds4, lifeEvent5, lifeEventAge5, requiredFunds5);
            advancedSetting = new AdvancedSetting(annualChangeMonth, annualChangeMoney, endingAge);
            params = new SimulationParams(id, expectedRateOfReturn, volatility, startAge, monthlySavings, initialValue, lifeEventParams, advancedSetting);

            // バリデーションの初期化
            lifeEventValidMessage = new lifeEventValidation();
            advancedSettingValidMessage = new advancedSettingValidation();
            validMessage = new Validation();
            validateFlg = false;

            return "redirect:/list";
        } catch (Exception e) {
            // requestParamのセットとバリデーションの初期化
            lifeEventStr lifeEventStr = new lifeEventStr(requestLifeEventAge1, requestRequiredFunds1, requestLifeEventAge2, requestRequiredFunds2, requestLifeEventAge3, requestRequiredFunds3, requestLifeEventAge4, requestRequiredFunds4, requestLifeEventAge5, requestRequiredFunds5);
            lifeEventValidMessage = new lifeEventValidation();
            advancedSettingStr advancedSettingStr = new advancedSettingStr(requestAnnualChangeMoney, requestEndingAge);
            advancedSettingValidMessage = new advancedSettingValidation();
            validMessage = new Validation();

            // エラー文言のセット
            validMessage.typeValid(requestExpectedRateOfReturn, requestVolatility, requestStartAge, requestMonthlySavings, requestInitialValue, lifeEventStr, lifeEventValidMessage, advancedSettingStr, advancedSettingValidMessage);
            validateFlg = true;

            return "redirect:/list";
        }
    }
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView getHome(ModelAndView mav) {
        mav.setViewName("mainpage");
        return mav;
    }

    private static int getAnnualChangeMonth(String annualChangePeriod) {
        return switch (annualChangePeriod) {
            case "6か月" -> 6;
            case "1年" -> 12;
            case "3年" -> 36;
            case "5年" -> 60;
            default -> 0;
        };
    }

}
