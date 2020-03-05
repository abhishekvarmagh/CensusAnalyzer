package censusanalyser;

public class CensusDTO {

    public String state;
    public int population;
    public double totalArea;
    public double populationDensity;
    public String stateCode;

    public CensusDTO(IndiaCensusCSV censusCSV) {
        this.state = censusCSV.state;
        this.population = censusCSV.population;
        this.totalArea = censusCSV.areaInSqKm;
        this.populationDensity = censusCSV.densityPerSqKm;
    }

    public CensusDTO(USCensusCSV censusCSV) {
        state = censusCSV.state;
        stateCode = censusCSV.stateId;
        population = censusCSV.population;
        totalArea = censusCSV.totalArea;
        populationDensity = censusCSV.populationDensity;
    }
}
