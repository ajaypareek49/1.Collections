package com.epam.processor;

import com.epam.data.RoadAccident;
import com.google.common.collect.*;
import com.google.common.math.LongMath;


import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;


import java.util.*;
import java.util.stream.Collectors;
//import static sun.cobra.Bridge.get;

/**
 * This is to be completed by mentees
 */
public class DataProcessor {

    private final List<RoadAccident> roadAccidentList;

    public DataProcessor(List<RoadAccident> roadAccidentList){
        this.roadAccidentList = roadAccidentList;
    }


//    First try to solve task using java 7 style for processing collections

    /**
     * Return road accident with matching index
     * @param index
     * @return
     */
    public RoadAccident getAccidentByIndex7(String index){
        for (RoadAccident ra: roadAccidentList){
            if (ra.getAccidentId().equals(index)){
                return ra;
            }
        }
        return null;

    }


    /**
     * filter list by longtitude and latitude values, including boundaries
     * @param minLongitude
     * @param maxLongitude
     * @param minLatitude
     * @param maxLatitude
     * @return
     */
    public Collection<RoadAccident> getAccidentsByLocation7(float minLongitude, float maxLongitude, float minLatitude, float maxLatitude) {
        List<RoadAccident> result = new ArrayList<>();
        for (RoadAccident ra : roadAccidentList) {
            if (ra.getLatitude() > minLatitude & ra.getLatitude() < maxLatitude
                    & ra.getLongitude() > minLongitude & ra.getLongitude() < maxLongitude) {
                result.add(ra);
            }
        }
        return result;
    }

    /**
     * count incidents by road surface conditions
     * ex:
     * wet -> 2
     * dry -> 5
     * @return
     */
    public Map<String, Long> getCountByRoadSurfaceCondition7(){
        Map<String, Long> map = new HashMap<>();
        for (RoadAccident ra: roadAccidentList){
            Long count = map.get(ra.getRoadSurfaceConditions());
            if (count == null){
                count = 1L;
            } else {
                count = count + 1;
            }
            map.put(ra.getRoadSurfaceConditions(), count);

        }
        System.out.println(map);
        return map;
    }

    /**
     * find the weather conditions which caused the top 3 number of incidents
     * as example if there were 10 accidence in rain, 5 in snow, 6 in sunny and 1 in foggy, then your result list should contain {rain, sunny, snow} - top three in decreasing order
     * @return
     */
    public List<String> getTopThreeWeatherCondition7(){
        Map<String, Long> conversionMap = new HashMap<>();
        for (RoadAccident ra: roadAccidentList){
            Long count = conversionMap.get(ra.getWeatherConditions());
            if (count == null){
                count = 1L;
            } else {
                count = count + 1;
            }
            conversionMap.put(ra.getWeatherConditions(),count);
        }

        TreeSet<Map.Entry<String, Long>> sortSet = new TreeSet<Map.Entry<String, Long>>(new Comparator<Map.Entry<String, Long>>() {
            @Override
            public int compare(Map.Entry<String, Long> o1, Map.Entry<String, Long> o2) {
                return (int)(o1.getValue() - o2.getValue());
            }
        });
        sortSet.addAll(conversionMap.entrySet());
        List<String> resultList = new ArrayList<>();
        for (int i = 0 ; i < 3 ; i++){
            resultList.add(sortSet.pollLast().getKey());
        }
        return resultList;
    }

    /**
     * return a multimap where key is a district authority and values are accident ids
     * ex:
     * authority1 -> id1, id2, id3
     * authority2 -> id4, id5
     * @return
     */
    public Multimap<String, String> getAccidentIdsGroupedByAuthority7(){
        Multimap result = ArrayListMultimap.create();
        for (RoadAccident ra: roadAccidentList) {
            result.put(ra.getDistrictAuthority(), ra.getAccidentId());
        }
        return result;
    }


    // Now let's do same tasks but now with streaming api



    public RoadAccident getAccidentByIndex(String index){
        return roadAccidentList.stream()
                .filter((r) -> r.getAccidentId()
                         .equals(index)).findAny().orElse(null);
    }


    /**
     * filter list by longtitude and latitude fields
     * @param minLongitude
     * @param maxLongitude
     * @param minLatitude
     * @param maxLatitude
     * @return
     */
    public Collection<RoadAccident> getAccidentsByLocation(float minLongitude, float maxLongitude, float minLatitude, float maxLatitude){
        return roadAccidentList.stream().filter(ra -> ra.getLatitude() > minLatitude & ra.getLatitude() < maxLatitude
                & ra.getLongitude() > minLongitude & ra.getLongitude() < maxLongitude)
                .collect(Collectors.toList());
    }

    /**
     * find the weather conditions which caused max number of incidents
     * @return
     */
    public List<String> getTopThreeWeatherCondition(){
        Map<String, Long> disorder = roadAccidentList.stream()
                .collect(Collectors.groupingBy(RoadAccident::getWeatherConditions,
                        Collectors.counting()));

        return disorder.entrySet().stream()
                .sorted((a,b) -> (int) (b.getValue() - a.getValue()))
                .limit(3).map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * count incidents by road surface conditions
     * @return
     */
    public Map<String, Long> getCountByRoadSurfaceCondition(){

        return roadAccidentList.stream()
                .collect(Collectors.groupingBy(RoadAccident::getRoadSurfaceConditions
                        , Collectors.counting()));
    }

    /**
     * To match streaming operations result, return type is a java collection instead of multimap
     * @return
     */
    public Map<String, List<String>> getAccidentIdsGroupedByAuthority(){

        return roadAccidentList.stream().collect(Collectors.groupingBy(RoadAccident::getDistrictAuthority,
                Collectors.mapping(RoadAccident::getAccidentId, Collectors.toList())
        ));
    }

}
