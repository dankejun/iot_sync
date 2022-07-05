package com.iot.sync.test;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Data;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class DeviceSoftwareVersion {
    private static final Map<String, String> map = Maps.newTreeMap();
    static Map<String, Map<String, String>> cycleMap = new HashMap<>();

    static {
        map.put("db.12613", "231");
        map.put("db.24882", "117");
        map.put("db.30771", "117");
        map.put("db.26425", "201");
        map.put("db.26672", "127");
        map.put("db.17458", "102");
        map.put("db.25140", "118");
        map.put("db.17460", "104");
        map.put("db.17463", "104");
        map.put("db.30774", "105");
        map.put("db.30002", "107");
        map.put("db.30003", "104");
        map.put("db.31286", "128");
        map.put("db.30776", "109");
        map.put("db.26673", "108");
        map.put("db.30260", "109");
        map.put("db.31284", "109");
        map.put("db.31283", "109");
        map.put("db.30000", "109");
        map.put("db.30519", "109");
        map.put("db.17718", "120");
        map.put("db.30008", "109");
        map.put("db.17456", "109");
        map.put("db.14148", "123");
        map.put("db.14149", "123");
        map.put("db.13892", "123");
        map.put("db.17719", "120");
        map.put("db.12870", "121");
        map.put("db.12614", "121");
        map.put("db.13124", "121");
        map.put("db.13127", "122");
        map.put("db.31288", "121");
        map.put("db.31289", "121");
        map.put("db.12868", "121");
        map.put("db.14404", "118");
        map.put("db.14660", "118");
        map.put("db.17205", "118");
        map.put("db.17206", "118");
        map.put("dc.12852", "109");
        map.put("dc.12594", "105");
        map.put("dc.13104", "109");

        Map<String, String> da = Maps.newTreeMap();
        Map<String, String> db = Maps.newTreeMap();
        Map<String, String> dc = Maps.newTreeMap();
        da.put("00", "standard");
        da.put("01", "fast");
        da.put("02", "blanket");
        da.put("03", "wool");
        da.put("04", "embathe");
        da.put("05", "memory");
        da.put("06", "child");
        da.put("07", "strong_wash");
        da.put("08", "down_jacket");
        da.put("09", "stir");
        da.put("0A", "mute");
        da.put("0B", "bucket_self_clean");
        da.put("0C", "air_dry");
        da.put("0D", "cycle");
        da.put("10", "remain_water");
        da.put("11", "summer");
        da.put("12", "big");
        da.put("13", "home");
        da.put("14", "cowboy");
        da.put("15", "soft");
        da.put("16", "hand_wash");
        da.put("17", "water_flow");
        da.put("18", "fog");
        da.put("19", "bucket_dry");
        da.put("1A", "fast_clean_wash");
        da.put("1B", "dehydration");
        da.put("1C", "under_wear");
        da.put("1D", "rinse_dehydration");
        da.put("1E", "five_clean");
        da.put("1F", "degerm");
        da.put("20", "in_15");
        da.put("21", "in_25");
        da.put("22", "love_baby");
        da.put("23", "outdoor");
        da.put("24", "silk");
        da.put("25", "shirt");
        da.put("26", "cook_wash");
        da.put("27", "towel");
        da.put("28", "memory_2");
        da.put("29", "memory_3");
        da.put("2A", "half_energy");
        da.put("2B", "all_energy");
        da.put("2C", "soft_wash");
        da.put("2D", "prevent_allergy");
        da.put("2E", "wash_cube");
        da.put("2F", "winter_jacket");
        da.put("30", "leisure_wash");
        da.put("31", "no_iron");
        da.put("32", "remove_mite_wash");
        da.put("33", "stubborn_stain");
        da.put("34", "silk_wash");
        da.put("35", "sterilize_wash");
        da.put("36", "cloud_wash");
        da.put("37", "smart");
        da.put("38", "speed_wash_30");
        da.put("39", "ai_intelligence_wash");
        da.put("3A", "mixed_wash");
        da.put("3B", "once_rinse");
        da.put("3C", "huxing_wash");

        db.put("00", "cotton");
        db.put("01", "eco");
        db.put("02", "fast_wash");
        db.put("03", "mixed_wash");
        db.put("05", "wool");
        db.put("07", "ssp");
        db.put("08", "sport_clothes");
        db.put("09", "single_dehytration");
        db.put("0A", "rinsing_dehydration");
        db.put("0B", "big");
        db.put("0C", "baby_clothes");
        db.put("0F", "down_jacket");
        db.put("10", "color");
        db.put("11", "intelligent");
        db.put("12", "quick_wash");
        db.put("1C", "shirt");
        db.put("04", "fiber");
        db.put("06", "enzyme");
        db.put("0D", "underwear");
        db.put("0E", "outdoor");
        db.put("15", "air_wash");
        db.put("16", "single_drying");
        db.put("1D", "steep");
        db.put("14 ", "water_cotton");
        db.put("17", "fast_wash_30");
        db.put("18", "fast_wash_60");
        db.put("1F", "water_mixed_wash");
        db.put("20", "water_fiber");
        db.put("21", "water_kids");
        db.put("22", "water_underwear");
        db.put("23", "specialist");
        db.put("FE", "love");
        db.put("19", "water_intelligent");
        db.put("1A", "water_steep");
        db.put("1B", "water_fast_wash_30");
        db.put("1E", "new_water_cotton");
        db.put("24", "water_eco");
        db.put("25", "wash_drying_60");
        db.put("26", "self_wash_5");
        db.put("27", "fast_wash_min");
        db.put("28", "mixed_wash_min");
        db.put("29", "dehydration_min");
        db.put("2A", "self_wash_min");
        db.put("2B", "baby_clothes_min");
        db.put("50", "diy0");
        db.put("51", "diy1");
        db.put("52", "diy2");
        db.put("65", "silk_wash");
        db.put("2C", "prevent_allergy");
        db.put("2D", "cold_wash");
        db.put("2E", "soft_wash");
        db.put("2F", "remove_mite_wash");
        db.put("30", "water_intense_wash");
        db.put("31", "fast_dry");
        db.put("32", "water_outdoor");
        db.put("33", "spring_autumn_wash");
        db.put("34", "summer_wash");
        db.put("35", "winter_wash");
        db.put("36", "jean");
        db.put("37", "new_clothes_wash");
        db.put("38", "silk");
        db.put("39", "insight_wash");
        db.put("3A", "fitness_clothes");
        db.put("3B", "mink");
        db.put("3C", "fresh_air");
        db.put("3D", "bucket_dry");
        db.put("3E", "jacket");
        db.put("3F", "bath_towel");
        db.put("40", "night_fresh_wash");
        db.put("60", "heart_wash");
        db.put("61", "water_cold_wash");
        db.put("62", "water_prevent_allergy");
        db.put("63", "water_remove_mite_wash");
        db.put("64", "water_ssp");
        db.put("66", "standard");
        db.put("67", "green_wool");
        db.put("68", "cook_wash");
        db.put("69", "fresh_remove_wrinkle");
        db.put("6A", "steam_sterilize_wash");
        db.put("6B", "aromatherapy");
        db.put("70", "sterilize_wash");
        db.put("83", "white_clothes_clean");
        db.put("84", "clean_stains");
        db.put("85", "tube_clean_all");
        db.put("86", "no_channeling_color");
        db.put("87", "scald_wash");
        db.put("88", "hanfu_spring_summer");
        db.put("89", "hanfu_autumn_winter");
        db.put("8B", "skin_care_wash");
        db.put("8D", "hanfu_wash");


        dc.put("00", "cotton");
        dc.put("01", "fiber");
        dc.put("02", "mixed_wash");
        dc.put("03", "jean");
        dc.put("04", "bedsheet");
        dc.put("05", "outdoor");
        dc.put("06", "down_jacket");
        dc.put("07", "plush");
        dc.put("08", "wool");
        dc.put("09", "dehumidify");
        dc.put("0A", "cold_air_fresh_air");
        dc.put("0B", "hot_air_dry");
        dc.put("0C", "sport_clothes");
        dc.put("0D", "underwear");
        dc.put("0E", "baby_clothes");
        dc.put("0F", "shirt");
        dc.put("10", "standard");
        dc.put("11", "quick_dry");
        dc.put("12", "fresh_air");
        dc.put("13", "low_temp_dry");
        dc.put("14", "eco_dry");
        dc.put("15", "quick_dry_30");
        dc.put("16", "towel");
        dc.put("17", "intelligent_dry");
        dc.put("18", "steam_care");
        dc.put("19", "big");
        dc.put("1A", "fixed_time_dry");
        dc.put("1B", "night_dry");
        dc.put("1C", "bracket_dry");
        dc.put("1D", "western_trouser");
        dc.put("1E", "dehumidification");
        dc.put("1F", "smart_dry");
        dc.put("20", "four_piece_suit");
        dc.put("21", "warm_clothes");
        dc.put("22", "quick_dry_20");
        dc.put("23", "steam_sterilize");
        dc.put("24", "enzyme");
        dc.put("25", "big_60");
        dc.put("26", "steam_no_iron");
        dc.put("27", "air_wash");
        dc.put("28", "bed_clothes");
        dc.put("29", "little_fast_dry");
        dc.put("2A", "small_piece_dry");
        dc.put("2B", "big_dry");
        dc.put("2C", "wool_nurse");
        dc.put("2D", "sun_quilt");
        dc.put("2E", "fresh_remove_smell");
        dc.put("2F", "bucket_self_clean");
        dc.put("30", "silk");
        dc.put("31", "sterilize");
        dc.put("32", "heavy_duty");
        dc.put("33", "towel_warmer");
        dc.put("34", "air_fluff");
        dc.put("35", "delicates");
        dc.put("36", "time_drying_30");
        dc.put("37", "time_drying_60");
        dc.put("38", "time_drying_90");
        dc.put("39", "dry_softnurse");
        dc.put("40", "uniforms");
        dc.put("41", "remove_electricity");

        cycleMap.put("dc", dc);
        cycleMap.put("db", db);

        cycleMap.put("da", da);
    }
    private static String translateCyclyeValue(String catCode,int code){
        String key= StringUtils.leftPad(Integer.toHexString(code),2,"0");
        return cycleMap.get(catCode.toLowerCase()).get(key.toUpperCase());
    }
    @Data
    public static class Cycle {

        private int value;
        private String modeName;

    }

    private static List<Cycle> readJson(String json) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        JsonNode node = mapper.readTree(json);
        JavaType type = mapper.getTypeFactory().constructCollectionType(List.class, Cycle.class);
        List<Cycle> list = Lists.newArrayList();
        if (node.findValue("paramData") != null) {
            list.addAll(mapper.convertValue(node.findValue("paramData"), type));
        }


        list = list.stream().sorted(Comparator.comparing(Cycle::getValue)).map(c -> {
            if ("漂+脱".equals(c.modeName)) {
                c.setModeName("漂洗+脱水");
            }
            return c;
        }).collect(Collectors.toList());
        return list;
    }

    @Test
    public void test() throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://wmspecs.oss-cn-hangzhou.aliyuncs.com/midea_json_new/%s/%s.json";
        File file = new File("D:\\guyl16\\cycleShop.md");

        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            String catCode = key.substring(0, 2).toUpperCase(Locale.ROOT);
            String jsonData = restTemplate.getForEntity(String.format(url, catCode, "0000." + key), String.class).getBody();

            String jsonDataNew = restTemplate.getForEntity(String.format(url, catCode, "0000." + key + "." + entry.getValue()), String.class).getBody();

            List<Cycle> cycles = readJson(jsonData);
            List<Cycle> cycles1 = readJson(jsonDataNew);
            List<Cycle> all;
            if (cycles.size() > cycles1.size()) {
                all = cycles.stream().filter(cycles1::contains).collect(Collectors.toList());
            } else if (cycles.size() < cycles1.size()) {
                all = cycles1.stream().filter(cycles::contains).collect(Collectors.toList());
            } else {
                all = new ArrayList<>(cycles);
            }


            FileUtils.writeLines(file, Lists.newArrayList("|A0 项目号|" + key + "|"), true);
            FileUtils.writeLines(file, Lists.newArrayList("|  ----  | ----  |"), true);

            FileUtils.writeLines(file, Lists.newArrayList("|默认版本|" + cycles.stream().map(c -> c.modeName + ":" + translateCyclyeValue(catCode,c.value)).collect(Collectors.joining(",")) + "|"), true);
            FileUtils.writeLines(file, Lists.newArrayList("|" + entry.getValue() + "版本|" + cycles1.stream().map(c -> c.modeName + ":" + translateCyclyeValue(catCode,c.value)).collect(Collectors.joining(",")) + "|"), true);

            FileUtils.writeLines(file, Lists.newArrayList("|交集|" + all.stream().distinct().map(c -> c.modeName + ":" +translateCyclyeValue(catCode,c.value)).collect(Collectors.joining(",")) + "|"), true);

            FileUtils.writeLines(file, Lists.newArrayList(""), true);

        }
    }


}
