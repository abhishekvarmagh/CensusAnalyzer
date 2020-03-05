package censusanalyser;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class CensusAnalyser {


    private List<CensusDTO> censusList = new ArrayList<>();
    private Map<String, CensusDTO> censusMap;

    public CensusAnalyser(){
        censusMap = new HashMap<>();
    }

    public int loadIndiaCensusData(String csvFilePath){
        try (Reader reader = Files.newBufferedReader(Paths.get(csvFilePath));) {
            ICSVBuilder icsvBuilder = CSVBuilderFactory.createBuilder();
            Iterator<IndiaCensusCSV> indiaCensusDTOIterator = icsvBuilder.getCSVFileIterator(reader, IndiaCensusCSV.class);
            while(indiaCensusDTOIterator.hasNext()){
                IndiaCensusCSV censusCSV = indiaCensusDTOIterator.next();
                censusMap.put(censusCSV.state, new CensusDTO(censusCSV));
            }
            censusList = censusMap.values().stream().collect(Collectors.toList());
            return censusMap.size();
        } catch (IOException e) {
            throw new CensusAnalyserException(e.getMessage(),
                    CensusAnalyserException.ExceptionType.CENSUS_FILE_PROBLEM);
        }
    }


    public int loadIndianStateCode(String csvFilePath){
        try (Reader reader = Files.newBufferedReader(Paths.get(csvFilePath));) {
            ICSVBuilder icsvBuilder = CSVBuilderFactory.createBuilder();
            Iterator<IndiaStateCodeCSV> stateCodeCSVIterator = icsvBuilder.
                                                                   getCSVFileIterator(reader, IndiaStateCodeCSV.class);
            Iterable<IndiaStateCodeCSV> csvIterable = () -> stateCodeCSVIterator;
            StreamSupport.stream(csvIterable.spliterator(), false)
                    .filter(csvState -> censusMap.get(csvState.stateName) != null)
                    .forEach(csvState -> censusMap.get(csvState.stateName).stateCode = csvState.stateCode);
            return censusMap.size();
        } catch (IOException e) {
            throw new CensusAnalyserException(e.getMessage(),
                    CensusAnalyserException.ExceptionType.CENSUS_FILE_PROBLEM);
        }
    }

    private <E> int getCount(Iterator<E> iterator){
        Iterable<E> csvIterable = () -> iterator;
        int numOfStateCode = (int) StreamSupport.stream(csvIterable.spliterator(), false).count();
        return numOfStateCode;
    }

    public String getStateWiseSortedCensusData() {
            if (censusList == null || censusList.size() == 0){
                throw new CensusAnalyserException("No Census Data",CensusAnalyserException.ExceptionType.NO_CENSUS_DATA);
            }
            Comparator<CensusDTO> censusCSVComparator = Comparator.comparing(census -> census.state);
            sort(censusCSVComparator);
            String sortedStateCensus = new Gson().toJson(censusList);
            return sortedStateCensus;
    }

    private void sort(Comparator<CensusDTO> censusCSVComparator) {
        for (int i = 0; i< censusList.size(); i++){
            for(int j = 0; j< censusList.size()-1-i; j++){
                CensusDTO censusDTO = censusList.get(j);
                CensusDTO censusDTO1 = censusList.get(j+1);
                if(censusCSVComparator.compare(censusDTO, censusDTO1) > 0){
                    censusList.set(j, censusDTO1);
                    censusList.set(j+1, censusDTO);
                }
            }
        }
    }

    public int loadUSCensusData(String csvFilePath) {
        try (Reader reader = Files.newBufferedReader(Paths.get(csvFilePath));) {
            ICSVBuilder icsvBuilder = CSVBuilderFactory.createBuilder();
            Iterator<USCensusCSV> usCensusCSVIterator = icsvBuilder.getCSVFileIterator(reader, USCensusCSV.class);
            while(usCensusCSVIterator.hasNext()){
                USCensusCSV censusCSV = usCensusCSVIterator.next();
                censusMap.put(censusCSV.state, new CensusDTO(censusCSV));
            }
            censusList = censusMap.values().stream().collect(Collectors.toList());
            return censusMap.size();
        } catch (IOException e) {
            throw new CensusAnalyserException(e.getMessage(),
                    CensusAnalyserException.ExceptionType.CENSUS_FILE_PROBLEM);
        }
    }
}
