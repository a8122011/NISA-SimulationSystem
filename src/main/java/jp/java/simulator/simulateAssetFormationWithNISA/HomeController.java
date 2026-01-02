package jp.java.simulator.simulateAssetFormationWithNISA;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

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
                            String selectedFund1, String selectedFund2, Integer weight1, Integer weight2,
                            LifeEventParams lifeEventParams, AdvancedSetting advancedSetting) {}

    record SimulationResult(
            SimulationParams params,
            List<List<Double>> valuationData,
            List<String> countList,
            double suggestedMax,
            int stepSize,
            boolean validateFlg,
            Validation validMessage,
            LifeEventValidation lifeEventValidMessage,
            AdvancedSettingValidation advancedSettingValidMessage
    ) {}

    @GetMapping("/") //初期表示 毎回リセット
    public String home(HttpSession session) {
        session.removeAttribute("result");
        return "mainpage";
    }

    @GetMapping("/list") //結果表示
    public String listItems(HttpSession session, Model model) {
        
        SimulationResult result =
            (SimulationResult) session.getAttribute("result");

        if (result != null) {
            model.addAttribute("params", result.params());
            
            int i = 0;
            for (List<Double> data : result.valuationData()) {
                switch (i) {
                    case 0 -> model.addAttribute("top30Percent", data);
                    case 1 -> model.addAttribute("median", data);
                    case 2 -> model.addAttribute("bottom30Percent", data);
                    case 3 -> model.addAttribute("bottom10Percent", data);
                    case 4 -> model.addAttribute("noOperation", data);
                }
                i++;
            }
            model.addAttribute("monthCountList", result.countList());
            model.addAttribute("suggestedMax", result.suggestedMax());
            model.addAttribute("stepSize", result.stepSize());

            model.addAttribute("expectedRateOfReturnError", result.validMessage().expectedRateOfReturnError);
            model.addAttribute("volatilityError", result.validMessage().volatilityError);
            model.addAttribute("startAgeError", result.validMessage().startAgeError);
            model.addAttribute("monthlySavingsError", result.validMessage().monthlySavingsError);
            model.addAttribute("initialValueError", result.validMessage().initialValueError);

            model.addAttribute("lifeEventAge1Error", result.lifeEventValidMessage().lifeEventAge1Error);
            model.addAttribute("requiredFunds1Error", result.lifeEventValidMessage().requiredFunds1Error);
            model.addAttribute("lifeEventAge2Error", result.lifeEventValidMessage().lifeEventAge2Error);
            model.addAttribute("requiredFunds2Error", result.lifeEventValidMessage().requiredFunds2Error);
            model.addAttribute("lifeEventAge3Error", result.lifeEventValidMessage().lifeEventAge3Error);
            model.addAttribute("requiredFunds3Error", result.lifeEventValidMessage().requiredFunds3Error);
            model.addAttribute("lifeEventAge4Error", result.lifeEventValidMessage().lifeEventAge4Error);
            model.addAttribute("requiredFunds4Error", result.lifeEventValidMessage().requiredFunds4Error);
            model.addAttribute("lifeEventAge5Error", result.lifeEventValidMessage().lifeEventAge5Error);
            model.addAttribute("requiredFunds5Error", result.lifeEventValidMessage().requiredFunds5Error);

            model.addAttribute("annualChangeMoneyError", result.advancedSettingValidMessage().annualChangeMoneyError);
            model.addAttribute("endingAgeError", result.advancedSettingValidMessage().endingAgeError);
        }
        return "mainpage";
    }

    @PostMapping("/add") //シミュレーション実行
    public String addItem(HttpSession session,
        @RequestParam String expectedRateOfReturn,
        @RequestParam String volatility,
        @RequestParam String startAge,
        @RequestParam String monthlySavings,
        @RequestParam String initialValue,

        @RequestParam(required = false, defaultValue = "") String lifeEvent1,
        @RequestParam(required = false, defaultValue = "") String lifeEventAge1,
        @RequestParam(required = false, defaultValue = "") String requiredFunds1,

        @RequestParam(required = false, defaultValue = "") String lifeEvent2,
        @RequestParam(required = false, defaultValue = "") String lifeEventAge2,
        @RequestParam(required = false, defaultValue = "") String requiredFunds2,

        @RequestParam(required = false, defaultValue = "") String lifeEvent3,
        @RequestParam(required = false, defaultValue = "") String lifeEventAge3,
        @RequestParam(required = false, defaultValue = "") String requiredFunds3,

        @RequestParam(required = false, defaultValue = "") String lifeEvent4,
        @RequestParam(required = false, defaultValue = "") String lifeEventAge4,
        @RequestParam(required = false, defaultValue = "") String requiredFunds4,

        @RequestParam(required = false, defaultValue = "") String lifeEvent5,
        @RequestParam(required = false, defaultValue = "") String lifeEventAge5,
        @RequestParam(required = false, defaultValue = "") String requiredFunds5,

        @RequestParam(required = false, defaultValue = "") String annualChangePeriod,
        @RequestParam(required = false, defaultValue = "") String annualChangeMoney,
        @RequestParam(required = false, defaultValue = "") String endingAge,
                          
        @RequestParam(required = false, defaultValue = "") String selectedFund1,
        @RequestParam(required = false, defaultValue = "") String selectedFund2,
        @RequestParam(required = false, defaultValue = "") String weight1,
        @RequestParam(required = false, defaultValue = "") String weight2
    ) {
        Validation validMessage = new Validation();
        LifeEventValidation lifeEventValidMessage = new LifeEventValidation();
        AdvancedSettingValidation advancedSettingValidMessage = new AdvancedSettingValidation();
        
        try {
            SimulationParams params = new SimulationParams(
                    UUID.randomUUID().toString().substring(0, 8),
                    Double.parseDouble(expectedRateOfReturn),
                    Double.parseDouble(volatility),
                    Integer.parseInt(startAge),
                    Double.parseDouble(monthlySavings),
                    Double.parseDouble(initialValue),
                selectedFund1,
                selectedFund2,
                parseInt(weight1),
                parseInt(weight2),
                
                    new LifeEventParams(
                            lifeEvent1,
                            parseInt(lifeEventAge1), parseDouble(requiredFunds1),
                            lifeEvent2,
                            parseInt(lifeEventAge2), parseDouble(requiredFunds2),
                            lifeEvent3,
                            parseInt(lifeEventAge3), parseDouble(requiredFunds3),
                            lifeEvent4,
                            parseInt(lifeEventAge4), parseDouble(requiredFunds4),
                            lifeEvent5,
                            parseInt(lifeEventAge5), parseDouble(requiredFunds5)
                    ),
                    new AdvancedSetting(
                            getAnnualChangeMonth(annualChangePeriod),
                            parseInt(annualChangeMoney),
                            parseInt(endingAge)
                    )
            );

            List<List<Double>> valuationData = Simulation.getValuationData(params);
            List<String> countList = Simulation.getAgeCountList(params);
            double suggestedMax = Simulation.getSuggestedMax(valuationData);
            double suggestedMin = Simulation.getSuggestedMin(valuationData);
            int stepSize = Simulation.getStepSize(suggestedMax,suggestedMin);

            session.setAttribute("result",
                    new SimulationResult(params, valuationData, countList, suggestedMax, stepSize, false, validMessage, lifeEventValidMessage, advancedSettingValidMessage)
            );

        } catch (Exception e) {
            validMessage.typeValid(
                    expectedRateOfReturn, volatility, startAge, monthlySavings, initialValue,
                    new LifeEventStr(lifeEventAge1, requiredFunds1, lifeEventAge2, requiredFunds2,
                            lifeEventAge3, requiredFunds3, lifeEventAge4, requiredFunds4,
                            lifeEventAge5, requiredFunds5),
                    lifeEventValidMessage,
                    new AdvancedSettingStr(annualChangeMoney, endingAge),
                    advancedSettingValidMessage,
                    weight1, weight2
            );

            session.setAttribute("result",
                    new SimulationResult(null, Collections.emptyList(), Collections.emptyList(), 0, 0, true, validMessage, lifeEventValidMessage, advancedSettingValidMessage)
            );
        }

        return "redirect:/list";
    }

    private static int parseInt(String s) {
        return s.isBlank() ? 0 : Integer.parseInt(s);
    }

    private static double parseDouble(String s) {
        return s.isBlank() ? 0 : Double.parseDouble(s);
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
