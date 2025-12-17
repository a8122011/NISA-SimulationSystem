package jp.java.simulator.simulateAssetFormationWithNISA;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/")
public class HomeController {

    /* =========================
       record 定義（既存流用）
       ========================= */
    record LifeEventParams(
            String lifeEvent1, int lifeEventAge1, double requiredFunds1,
            String lifeEvent2, int lifeEventAge2, double requiredFunds2,
            String lifeEvent3, int lifeEventAge3, double requiredFunds3,
            String lifeEvent4, int lifeEventAge4, double requiredFunds4,
            String lifeEvent5, int lifeEventAge5, double requiredFunds5
    ) {}

    record AdvancedSetting(
            int annualChangeMonth,
            int annualChangeMoney,
            int endingAge
    ) {}

    record SimulationParams(
            String id,
            double expectedRateOfReturn,
            double volatility,
            int startAge,
            double monthlySavings,
            double initialValue,
            LifeEventParams lifeEventParams,
            AdvancedSetting advancedSetting
    ) {}

    /* =========================
       初期表示
       ========================= */
    @GetMapping("/")
    public String index() {
        return "mainpage";
    }

    /* =========================
       シミュレーション結果表示
       ========================= */
    @GetMapping("/list")
    public String list(Model model, HttpSession session) {

        SimulationParams params =
                (SimulationParams) session.getAttribute("params");

        if (params == null) {
            return "mainpage";
        }

        List<List<Double>> valuationData =
                Simulation.getValuationData(params);

        model.addAttribute("params", params);
        model.addAttribute("top30Percent", valuationData.get(0));
        model.addAttribute("median", valuationData.get(1));
        model.addAttribute("bottom30Percent", valuationData.get(2));
        model.addAttribute("bottom10Percent", valuationData.get(3));
        model.addAttribute("noOperation", valuationData.get(4));

        List<String> countList =
                Simulation.getAgeCountList(params);

        model.addAttribute("monthCountList", countList);

        double suggestedMax =
                Simulation.getSuggestedMax(valuationData);

        model.addAttribute("suggestedMax", suggestedMax);
        model.addAttribute("stepSize",
                Simulation.getStepSize(suggestedMax));

        return "mainpage";
    }

    /* =========================
       入力 → Session保存
       ========================= */
    @PostMapping("/add")
    public String add(
            @RequestParam double expectedRateOfReturn,
            @RequestParam double volatility,
            @RequestParam int startAge,
            @RequestParam double monthlySavings,
            @RequestParam double initialValue,

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

            HttpSession session,
            RedirectAttributes redirect
    ) {

        LifeEventParams lifeEventParams = new LifeEventParams(
                lifeEvent1,
                parseInt(lifeEventAge1),
                parseDouble(requiredFunds1),
                lifeEvent2,
                parseInt(lifeEventAge2),
                parseDouble(requiredFunds2),
                lifeEvent3,
                parseInt(lifeEventAge3),
                parseDouble(requiredFunds3),
                lifeEvent4,
                parseInt(lifeEventAge4),
                parseDouble(requiredFunds4),
                lifeEvent5,
                parseInt(lifeEventAge5),
                parseDouble(requiredFunds5)
        );

        AdvancedSetting advancedSetting = new AdvancedSetting(
                getAnnualChangeMonth(annualChangePeriod),
                parseInt(annualChangeMoney),
                parseInt(endingAge)
        );

        SimulationParams params = new SimulationParams(
                UUID.randomUUID().toString().substring(0, 8),
                expectedRateOfReturn,
                volatility,
                startAge,
                monthlySavings,
                initialValue,
                lifeEventParams,
                advancedSetting
        );

        session.setAttribute("params", params);

        return "redirect:/list";
    }

    /* =========================
       Utility
       ========================= */
    private static int parseInt(String value) {
        if (value == null || value.isBlank()) return 0;
        return Integer.parseInt(value);
    }

    private static double parseDouble(String value) {
        if (value == null || value.isBlank()) return 0;
        return Double.parseDouble(value);
    }

    private static int getAnnualChangeMonth(String period) {
        return switch (period) {
            case "6か月" -> 6;
            case "1年" -> 12;
            case "3年" -> 36;
            case "5年" -> 60;
            default -> 0;
        };
    }
}
