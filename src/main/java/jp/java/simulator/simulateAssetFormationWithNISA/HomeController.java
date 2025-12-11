package jp.java.simulator.simulateAssetFormationWithNISA;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.util.*;

@Controller
public class HomeController {

    // -----------------------------
    // record 定義（DTO）
    // -----------------------------
    record LifeEventParams(String lifeEvent1, int lifeEventAge1, double requiredFunds1,
                           String lifeEvent2, int lifeEventAge2, double requiredFunds2,
                           String lifeEvent3, int lifeEventAge3, double requiredFunds3,
                           String lifeEvent4, int lifeEventAge4, double requiredFunds4,
                           String lifeEvent5, int lifeEventAge5, double requiredFunds5) {}

    record AdvancedSetting(int annualChangeMonth, int annualChangeMoney, int endingAge) {}

    record SimulationParams(String id, double expectedRateOfReturn, double volatility,
                            int startAge, double monthlySavings, double initialValue,
                            LifeEventParams lifeEventParams, AdvancedSetting advancedSetting) {}

    // -----------------------------
    // 初期表示（mainpage）
    // -----------------------------
    @GetMapping(value = {"/", "/mainpage"})
    public String showMainpage(Model model) {

        model.addAttribute("time", LocalDateTime.now());
        model.addAttribute("simulationHistory", new ArrayList<>());

        return "mainpage";
    }


    // -----------------------------
    // /add → 計算 → mainpage に表示
    // -----------------------------
    @GetMapping("/add")
    public String addItem(
            @RequestParam("expectedRateOfReturn") String requestExpectedRateOfReturn,
            @RequestParam("volatility") String requestVolatility,
            @RequestParam("startAge") String requestStartAge,
            @RequestParam("monthlySavings") String requestMonthlySavings,
            @RequestParam("initialValue") String requestInitialValue,

            @RequestParam("lifeEvent1") String lifeEvent1,
            @RequestParam("lifeEventAge1") String requestLifeEventAge1,
            @RequestParam("requiredFunds1") String requestRequiredFunds1,

            @RequestParam("lifeEvent2") String lifeEvent2,
            @RequestParam("lifeEventAge2") String requestLifeEventAge2,
            @RequestParam("requiredFunds2") String requestRequiredFunds2,

            @RequestParam("lifeEvent3") String lifeEvent3,
            @RequestParam("lifeEventAge3") String requestLifeEventAge3,
            @RequestParam("requiredFunds3") String requestRequiredFunds3,

            @RequestParam("lifeEvent4") String lifeEvent4,
            @RequestParam("lifeEventAge4") String requestLifeEventAge4,
            @RequestParam("requiredFunds4") String requestRequiredFunds4,

            @RequestParam("lifeEvent5") String lifeEvent5,
            @RequestParam("lifeEventAge5") String requestLifeEventAge5,
            @RequestParam("requiredFunds5") String requestRequiredFunds5,

            @RequestParam("annualChangePeriod") String annualChangePeriod,
            @RequestParam("annualChangeMoney") String requestAnnualChangeMoney,

            @RequestParam("endingAge") String requestEndingAge,
            @RequestParam("weight1") String requestweight1,
            @RequestParam("weight2") String requestweight2,

            Model model
    ) {

        String id = UUID.randomUUID().toString().substring(0, 8);

        try {
            // ----------------------------------
            // 必須項目の変換
            // ----------------------------------
            double expectedRateOfReturn = Double.parseDouble(requestExpectedRateOfReturn);
            double volatility = Double.parseDouble(requestVolatility);
            int startAge = Integer.parseInt(requestStartAge);
            double monthlySavings = Double.parseDouble(requestMonthlySavings);
            double initialValue = Double.parseDouble(requestInitialValue);

            // ----------------------------------
            // ライフイベント項目
            // ----------------------------------
            int lifeEventAge1 = parseOrZero(requestLifeEventAge1);
            double requiredFunds1 = parseOrZeroDouble(requestRequiredFunds1);

            int lifeEventAge2 = parseOrZero(requestLifeEventAge2);
            double requiredFunds2 = parseOrZeroDouble(requestRequiredFunds2);

            int lifeEventAge3 = parseOrZero(requestLifeEventAge3);
            double requiredFunds3 = parseOrZeroDouble(requestRequiredFunds3);

            int lifeEventAge4 = parseOrZero(requestLifeEventAge4);
            double requiredFunds4 = parseOrZeroDouble(requestRequiredFunds4);

            int lifeEventAge5 = parseOrZero(requestLifeEventAge5);
            double requiredFunds5 = parseOrZeroDouble(requestRequiredFunds5);

            // ----------------------------------
            // 詳細設定
            // ----------------------------------
            int annualChangeMonth = getAnnualChangeMonth(annualChangePeriod);
            int annualChangeMoney = parseOrZero(requestAnnualChangeMoney);
            int endingAge = parseOrZero(requestEndingAge);

            // DTO セット
            LifeEventParams lifeEventParams = new LifeEventParams(
                    lifeEvent1, lifeEventAge1, requiredFunds1,
                    lifeEvent2, lifeEventAge2, requiredFunds2,
                    lifeEvent3, lifeEventAge3, requiredFunds3,
                    lifeEvent4, lifeEventAge4, requiredFunds4,
                    lifeEvent5, lifeEventAge5, requiredFunds5
            );

            AdvancedSetting advancedSetting = new AdvancedSetting(annualChangeMonth, annualChangeMoney, endingAge);

            SimulationParams params = new SimulationParams(
                    id, expectedRateOfReturn, volatility, startAge,
                    monthlySavings, initialValue, lifeEventParams, advancedSetting
            );

            // ----------------------------------
            // シミュレーション実行
            // ----------------------------------
            List<List<Double>> valuationData = Simulation.getValuationData(params);
            List<String> countList = Simulation.getAgeCountList(params);
            double suggestedMax = Simulation.getSuggestedMax(valuationData);
            int stepSize = Simulation.getStepSize(suggestedMax);

            // ----------------------------------
            // model に全てセット → mainpage に返す
            // ----------------------------------
            model.addAttribute("params", params);
            model.addAttribute("monthCountList", countList);
            model.addAttribute("valuationData", valuationData);
            model.addAttribute("suggestedMax", suggestedMax);
            model.addAttribute("stepSize", stepSize);

            return "mainpage";
        }
        catch (Exception e) {
            model.addAttribute("error", "入力に誤りがあります");
            return "mainpage";
        }
    }


    // -----------------------------
    // 補助メソッド
    // -----------------------------
    private static int parseOrZero(String s) {
        if (s == null || s.isEmpty()) return 0;
        return Integer.parseInt(s);
    }

    private static double parseOrZeroDouble(String s) {
        if (s == null || s.isEmpty()) return 0;
        return Double.parseDouble(s);
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
