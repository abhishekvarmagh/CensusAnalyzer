package censusanalyser;

import com.google.gson.Gson;

import java.util.*;
import java.util.stream.Collectors;

public class CensusAnalyser {

    public enum Country {INDIA, US;}

    List<CensusDTO> censusList;
    Map<String, CensusDTO> censusMap;
    Map<SortField, Comparator<CensusDTO>> sortMap;

    public CensusAnalyser() {
        sortMap = new HashMap<>();
        sortMap.put(SortField.STATE, Comparator.comparing(census -> census.state));
        sortMap.put(SortField.POPULATION, Comparator.comparing(census -> census.population));
        sortMap.put(SortField.TOTAL_AREA, Comparator.comparing(census -> census.totalArea));
        sortMap.put(SortField.POPULATION_DENSITY, Comparator.comparing(census -> census.populationDensity));
        sortMap.put(SortField.STATE_CODE, Comparator.comparing(census -> census.stateCode));
    }

    public int loadCensusData(Country country, String... csvFilePath) throws CensusAnalyserException {
        censusMap = CensusAdapterFactory.getCensusAdapter(country, csvFilePath);
        return censusMap.size();
    }

    public String getStateWiseSortedCensusData(SortField sortField) {
        censusList = censusMap.values().stream().collect(Collectors.toList());
        if (censusList == null || censusList.size() == 0) {
            throw new CensusAnalyserException("No Census Data", CensusAnalyserException.ExceptionType.NO_CENSUS_DATA);
        }
        Comparator<CensusDTO> censusCSVComparator = Comparator.comparing(census -> census.state);
        this.sort(this.sortMap.get(sortField).reversed());
        String sortedStateCensus = new Gson().toJson(censusList);
        return sortedStateCensus;
    }

    private void sort(Comparator<CensusDTO> censusCSVComparator) {
        for (int i = 0; i < censusList.size(); i++) {
            for (int j = 0; j < censusList.size() - 1 - i; j++) {
                CensusDTO censusDTO = censusList.get(j);
                CensusDTO censusDTO1 = censusList.get(j + 1);
                if (censusCSVComparator.compare(censusDTO, censusDTO1) > 0) {
                    censusList.set(j, censusDTO1);
                    censusList.set(j + 1, censusDTO);
                }
            }
        }
    }

}
