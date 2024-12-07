package com.example.demo.service.imp;

import com.example.demo.exception.EmptyRecordException;
import com.example.demo.exception.EmptyTableException;
import com.example.demo.exception.SaveRecordFailException;
import com.example.demo.model.agriculture.Forecast;
import com.example.demo.model.agriculture.Sensor;
import com.example.demo.model.agriculture.SensorRecord;
import com.example.demo.persistence.SensorRecordRepository;
import com.example.demo.persistence.SensorRepository;
import com.example.demo.service.SensorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SensorServiceImp implements SensorService {

    @Autowired
    private SensorRepository sensorRepository;

    @Autowired
    private SensorRecordRepository sensorRecordRepository;

    /**
     * Get all sensors from database, if the table is empty then throw an
     * exception.
     * @return List of the sensors founded in the database.
     * @exception EmptyTableException When the table have not records.
     */
    @Override
    public List<Sensor> getAllSensors() {
        List<Sensor> sensors = sensorRepository.findAll();
        if (sensors.isEmpty()) throw new EmptyTableException(Sensor.class);
        return sensors;
    }

    /**
     * Get all records of a sensor from database, if the table is empty then
     * throw an exception.
     * @return List of all the records of a sensor founded in the database.
     * @exception EmptyTableException When the table have not records.
     */
    @Override
    public List<SensorRecord> getAllRecords() {
        List<SensorRecord> records = sensorRecordRepository.findAll();
        if (records.isEmpty()) throw new EmptyTableException(SensorRecord.class);
        return records;
    }

    /**
     * Get the most recent record of a sensor from database, if the record is
     * empty then throw an exception.
     * @param sensor The sensor in which it gets the recent record.
     * @return The recent record of a sensor founded in the database.
     * @exception EmptyRecordException When the record is empty.
     */
    @Override
    public SensorRecord getMostRecentRecord(Sensor sensor) {
        Optional<SensorRecord> recentRecord = sensorRecordRepository.findMostRecentRecordBySensor(sensor);
        if (recentRecord.isEmpty()) throw new EmptyRecordException(SensorRecord.class);
        return recentRecord.get();
    }

    /**
     * Create or update a sensor record in the database, if the record is not
     * saved then throw an exception.
     * @param sensorRecord The record that will be saved.
     * @exception SaveRecordFailException When the record couldn't been saved.
     */
    @Override
    public void saveSensorRecord(SensorRecord sensorRecord) {
        SensorRecord savedRecord = sensorRecordRepository.save(sensorRecord);
        if (savedRecord.getId() == null) throw new SaveRecordFailException(SensorRecord.class);
    }
}
