package com.company.preparers;

import com.company.ConnectionAdapter;
import com.company.parsers.DateFactory;
import com.company.reservations.FR;
import com.company.reservations.FR3;
import com.company.utilities.Pair;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.company.executors.FR3Executor.*;

public class FR3Preparer{


    private static final String FR3_FOLDER = "FR3";
    private FR3Preparer(){}

    public static PreparedStatement select(int RES_CODE)
            throws  Exception
    {

        String query;
        query = new String(Files.readAllBytes(Paths.get(FR3_FOLDER + "/"+"FR3_RES_CODE.sql")));
        PreparedStatement statement = ConnectionAdapter.getConnection().prepareStatement(query);
        statement.setInt(1, RES_CODE);
        return statement;
    }

    public static PreparedStatement update(Map<String, String> fieldValues, List<String> fields, double rate, int RES_CODE)
            throws Exception
    {
        final PreparedStatement statement = ConnectionAdapter.getConnection().prepareStatement(
                "UPDATE lab7_reservations " +
                        "SET FirstName = ?," +
                        "LastName = ?," +
                        "CheckIn = ?,"+
                        "Checkout = ?,"+
                        "Kids = ?,"+
                        "Adults = ?,"+
                        "Rate = ? " +
                        "WHERE CODE = ?;"
        );
        final Date CheckIn = DateFactory.StringToDate(fieldValues.get(fields.get(BEGIN_STAY)));
        final Date CheckOut = DateFactory.StringToDate(fieldValues.get(fields.get(END_STAY)));

        statement.setString(1, fieldValues.get(fields.get(FIRST_NAME)));
        statement.setString(2, fieldValues.get(fields.get(LAST_NAME)));
        statement.setDate(3, new java.sql.Date(CheckIn.getTime()));
        statement.setDate(4, new java.sql.Date(CheckOut.getTime()));
        statement.setInt(5, Integer.parseInt(fieldValues.get(fields.get(KIDS))));
        statement.setInt(6, Integer.parseInt(fieldValues.get(fields.get(ADULTS))));
        statement.setDouble(7, rate);
        statement.setInt(8, RES_CODE);
        return statement;

    }


}
