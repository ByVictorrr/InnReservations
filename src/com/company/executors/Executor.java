package com.company.executors;

import com.company.ConnectionAdapter;
import com.company.parsers.DateFactory;
import com.company.preparers.*;
import com.company.reservations.*;
import com.company.validators.FR2Validator;
import com.company.validators.FR3Validator;
import com.company.validators.FR4Validator;
import com.company.validators.Validator;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

abstract public class Executor {

    /**
     * These contants below are used to valid inputs
     */
    public static List<Integer> RES_CODES;
    public final static Integer MAX_OCC;
    public final static List<String> ROOM_CODES;
    public final static List<String> BED_TYPES;
    public static Integer NEXT_RES_CODE;

    static {
        List<ResultSet> rs = new ArrayList<>();
        Integer _MAX_OCC = 0;
        List<String> _ROOM_CODES = null, _BED_TYPES = null;
        List<Integer> _RES_CODES = null;
        Integer _NEXT_RES_CODE = 0;

        try {
            rs.add(ConstantsPreparer.MAX_OCC().executeQuery());
            rs.add(ConstantsPreparer.MAX_RES_CODE().executeQuery());
            rs.add(ConstantsPreparer.RES_CODES().executeQuery());
            rs.add(ConstantsPreparer.BED_TYPES().executeQuery());
            rs.add(ConstantsPreparer.ROOM_TYPES().executeQuery());
            rs.get(0).next();
            rs.get(1).next();
            _MAX_OCC = rs.get(0).getInt(1);
            _NEXT_RES_CODE = rs.get(1).getInt(1)+1;
            _RES_CODES = GET_LIST(rs.get(2))
                    .stream()
                    .map(r->(Integer)r)
                    .collect(Collectors.toList());
            _BED_TYPES = GET_LIST(rs.get(3))
                    .stream()
                    .map(b->(String)b)
                    .collect(Collectors.toList());
            _ROOM_CODES = GET_LIST(rs.get(4))
                    .stream()
                    .map(r->(String)r)
                    .collect(Collectors.toList());
        }catch (SQLException e) {
            e.printStackTrace();
        }
        MAX_OCC = _MAX_OCC;
        ROOM_CODES=_ROOM_CODES;
        RES_CODES = _RES_CODES;
        BED_TYPES=_BED_TYPES;
        NEXT_RES_CODE=_NEXT_RES_CODE;
    }

    abstract public void execute();


    /**
     * gets the value of the field_names of each field
     * @param field_names - the field names want a user to input
     * @return map that maps the field names to value or null if they want to go back to main menu
     */
    protected static HashMap<String, String> getFields(List<String> field_names, Validator validator)
        throws Exception
    {
        int counter=0;
        final HashMap<String, String> fields = new HashMap<>(field_names.size());
        validator.setFields(field_names);
        validator.setFieldsValues(fields);
        String value;
        while(counter < field_names.size()){
            System.out.println("input " + field_names.get(counter) + "(c - return to main menu): ");
            if((value = new Scanner(System.in).next()).equals( "c")){
                return null;
            }
            // check to see if a field is valid
            if(!validator.valid(counter, value)){
                continue;
            }
            fields.put(field_names.get(counter), value);
            counter++;
        }
        return fields;
    }


    protected static Map<Integer, FR> getReservations(ResultSet rs, FR instance) throws Exception{
        ResultSetMetaData rsmd = rs.getMetaData();
        int col_num = rsmd.getColumnCount();
        Map<Integer, FR> res = new HashMap<>();
        int id=0;
        while (rs.next()){
            FR f = null;
            if(instance instanceof FR2){
                f = new FR2();
            }else if(instance instanceof FR3){
                f = new FR3();
            }else if(instance instanceof  FR4){
                f = new FR4();
            }else if(instance instanceof  FR6){
                f= new FR6();
            }
            for (int i=1; i <= col_num; i++){
                f.setField(rsmd.getColumnName(i), rs.getString(i));
            }
            res.put(id++, f);
        }
        return res;
    }
    protected static void printResultSet(ResultSet rs) throws SQLException{
        ResultSetMetaData rsmd = rs.getMetaData();
        int col_num = rsmd.getColumnCount();
        boolean isFirst = true;
        StringBuilder col_names = new StringBuilder();
        while (rs.next()){
            StringBuilder values = new StringBuilder();
            for (int i=1; i <= col_num; i++){
                if(i > 1){
                    values.append(", ");
                    if(isFirst) col_names.append(", ");
                }
                if (isFirst) col_names.append(rsmd.getColumnName(i));

                values.append(rs.getString(i));
            }
            if(isFirst){
                System.out.println(col_names);
                isFirst=false;
            }
            System.out.println(values);
        }
    }


    /**
     * @param rs - query results
     * @return returns a list for LIMIT VARIBLES
     * @throws SQLException
     */
    private static List<Object> GET_LIST(ResultSet rs)
            throws SQLException {
        List<Object> LIST = new ArrayList<>();
        while (rs.next()) {
            LIST.add(rs.getObject(1));
        }
        return LIST;
    }


    public static boolean isMyResultSetEmpty(ResultSet rs) throws SQLException {
            if(rs == null){
                return true;
            }
        return (!rs.isBeforeFirst() && rs.getRow() == 0);
    }


}

