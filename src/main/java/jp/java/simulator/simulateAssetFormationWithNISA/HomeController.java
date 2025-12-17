package jp.java.simulator.simulateAssetFormationWithNISA;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpSession;
import java.util.*;

@Controller
public class HomeController {

    /* =====================
       record 定義
       ===================== */
    record LifeEventParams(String lifeEvent1, int lifeEventAge1, double requiredFunds1,
                           String lifeEvent2, int lifeEventAge2, double requiredFunds2,
                           String lifeEvent3, int lifeEventAge3, double requiredFunds3,
                           String lifeEvent4, int lifeEventAge4, double requiredFunds4,
                           String lifeEvent5, int lifeEventAge5, double requiredFunds5) {}

    record AdvancedSetting(int annualChangeMonth, int annualChangeMoney, int endingAge) {}

    record SimulationParams(String id, double expectedRateOfReturn, double volatility,
                            int startAge, double monthlySavings, double initialValue,
                            LifeEventParams lifeEventParams, AdvancedSetting advancedSetting) {}

    /* =====================
       トップ表示
       ===================== */
    @GetMapping({"/", "/list"})
    public String list(Model model, HttpSession session) {

        SimulationParams params = (SimulationParams) session.getAttribute("params");
        Boolean validateFlg = (Boolean) session.getAttribute("validateFlg");

        if (params != null && Boolean.FALSE.equals(validateFlg)) {
            List<List<Double>> valuationData = Simulation.getValuationData(params);
            List<String> countList = Simulation.getAgeCountList(params);
            double suggestedMax = Simulation.getSuggestedMax(valuationData);
            int stepSize = Simulation.getStepSize(suggestedMax);

            model.addAttribute("params", params);
            model.addAttribute("top30Percent", valuationData.get(0));
            model.addAttribute("median", valuationData.get(1));
            model.addAttribute("bottom30Percent", valuationData.get(2));
            model.addAttribute("bottom10Percent", valuationData.get(3));
            model.addAttribute("noOperation", valuationData.get(4));
            model.addAttribute("monthCountList", countList);
            model.addAttribute("suggestedMax", suggestedMax);
            model.addAttribute("stepSize", stepSize);
        }

        model.addAttribute("validMessage", session.getAttribute("validMessage"));
        model.addAttribute("lifeEventValidMessage", session.getAttribute("lifeEventValidMessage"));
        model.addAttribute("advancedSettingValidMessage", session.getAttribute("advancedSettingValidMessage"));

        return "mainpage";
    }

    /* =====================
       シミュレーション実行
       ===================== */
    @GetMapping("/add")
    public String add(
            @RequestParam String expectedRateOfReturn,
            @RequestParam String volatility,
            @RequestParam String startAge,
            @RequestParam String monthlySavings,
            @RequestParam(required = false, defaultValue = "0") String initialValue,

            @RequestParam(required = false, defaultValue = "") String lifeEvent1,
            @RequestParam(required = false, defaultValue = "") String lifeage1,
            @RequestParam(required = false, defaultValue = "") String requiredFunds1,

            @RequestParam(required = false, defaultValue = "") String lifeEvent2,
            @RequestParam(required = false, defaultValue = "") String lifeage2,
            @RequestParam(required = false, defaultValue = "") String requiredFunds2,

            @RequestParam(required = false, defaultValue = "") String lifeEvent3,
            @RequestParam(required = false, defaultValue = "") String lifeage3,
            @RequestParam(required = false, defaultValue = "") String requiredFunds3,

            @RequestParam(required = false, defaultValue = "") String lifeEvent4,
            @RequestParam(required = false, defaultValue = "") String lifeage4,
            @RequestParam(required = false, defaultValue = "") String requiredFunds4,

            @RequestParam(required = false, defaultValue = "") String lifeEvent5,
            @RequestParam(required = false, defaultValue = "") String lifeage5,
            @RequestParam(required = false, defaultValue = "") String requiredFunds5,

            @RequestParam(required = false, defaultValue = "") String annualChangePeriod,
            @RequestParam(required = false, defaultValue = "") String annualChangeMoney,
            @RequestParam(required = false, defaultValue = "") String endingAge,

            HttpSession session
    ) {
        try {
            double rate = Double.parseDouble(expectedRateOfReturn);
            double vola = Double.parseDouble(volatility);
            int age = Integer.parseInt(startAge);
            double saving = Double.parseDouble(monthlySavings);
            double init = Double.parseDouble(initialValue);

            LifeEventParams lifeEventParams = new LifeEventParams(
                    lifeEvent1, parseIntSafe(lifeage1), parseDoubleSafe(requiredFunds1),
                    lifeEvent2, parseIntSafe(lifeage2), parseDoubleSafe(requiredFunds2),
                    lifeEvent3, parseIntSafe(lifeage3), parseDoubleSafe(requiredFunds3),
                    lifeEvent4, parseIntSafe(lifeage4), parseDoubleSafe(requiredFunds4),
                    lifeEvent5, parseIntSafe(lifeage5), parseDoubleSafe(requiredFunds5)
            );

            AdvancedSetting advancedSetting = new AdvancedSetting(
                    getAnnualChangeMonth(annualChangePeriod),
                    parseIntSafe(annualChangeMoney),
                    parseIntSafe(endingAge)
            );

            SimulationParams params = new SimulationParams(
                    UUID.randomUUID().toString(), rate, vola, age, saving, init,
                    lifeEventParams, advancedSetting
            );

            session.setAttribute("params", params);
            session.setAttribute("validateFlg", false);

            return "redirect:/list";

        } catch (Exception e) {
            session.setAttribute("validateFlg", true);
            return "redirect:/list";
        }
    }

    /* =====================
       util
       ===================== */
    private static int parseIntSafe(String s) {
        if (s == null || s.isBlank()) return 0;
        return Integer.parseInt(s);
    }

    private static double parseDoubleSafe(String s) {
        if (s == null || s.isBlank()) return 0;
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
