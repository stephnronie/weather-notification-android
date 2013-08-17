package ru.gelin.android.weather.openweathermap;

import android.test.AndroidTestCase;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import ru.gelin.android.weather.*;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class OpenWeatherMapWeatherTest extends AndroidTestCase {

    public void testNotEmpty() throws WeatherException, IOException, JSONException {
        OpenWeatherMapWeather weather = new OpenWeatherMapWeather(getContext(), readJSON("omsk_name_2.5.json"));
        assertNotNull(weather);
        assertFalse(weather.isEmpty());
    }

    public void testNotNullLocation() {
        OpenWeatherMapWeather weather = new OpenWeatherMapWeather(getContext());
        assertNotNull(weather.getLocation());
        assertTrue(weather.isEmpty());
    }

    public void testGetTemperature() throws IOException, JSONException, WeatherException {
        OpenWeatherMapWeather weather = new OpenWeatherMapWeather(getContext(), readJSON("omsk_name_2.5.json"));
        WeatherCondition condition = weather.getConditions().get(0);
        assertEquals(21, condition.getTemperature(TemperatureUnit.C).getCurrent());
    }

    public void testParseEmptyJSON() throws JSONException {
        JSONTokener parser = new JSONTokener("{}");
        try {
            new OpenWeatherMapWeather(getContext(), (JSONObject)parser.nextValue());
            fail();
        } catch (WeatherException e) {
            //passed
        }
    }

    public void testGetLocation() throws IOException, JSONException, WeatherException {
        OpenWeatherMapWeather weather = new OpenWeatherMapWeather(getContext(), readJSON("omsk_name_2.5.json"));
        Location location = weather.getLocation();
        assertNotNull(location);
        assertEquals("Omsk", location.getText());
    }

    public void testGetTime() throws IOException, JSONException, WeatherException {
        OpenWeatherMapWeather weather = new OpenWeatherMapWeather(getContext(), readJSON("omsk_name_2.5.json"));
        Date time = weather.getTime();
        assertNotNull(time);
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        calendar.set(2013, Calendar.AUGUST, 15, 14, 00, 00);
        calendar.set(Calendar.MILLISECOND, 0);
        assertEquals(calendar.getTime(), time);
    }

    public void testGetQueryTime() throws IOException, JSONException, WeatherException {
        long now = System.currentTimeMillis();
        OpenWeatherMapWeather weather = new OpenWeatherMapWeather(getContext(), readJSON("omsk_name_2.5.json"));
        assertTrue(now < weather.getQueryTime().getTime());
    }

    public void testGetConditionText() throws IOException, JSONException, WeatherException {
        OpenWeatherMapWeather weather = new OpenWeatherMapWeather(getContext(), readJSON("omsk_name_2.5.json"));
        WeatherCondition condition = weather.getConditions().get(0);
        String text = condition.getConditionText();
        assertEquals("Sky is clear", text);
    }

    public void testGetLowTemperature() throws IOException, JSONException, WeatherException {
        OpenWeatherMapWeather weather = new OpenWeatherMapWeather(getContext(), readJSON("omsk_name_2.5.json"));
        WeatherCondition condition = weather.getConditions().get(0);
        assertEquals(21, condition.getTemperature(TemperatureUnit.C).getLow());
    }

    public void testGetHighTemperature() throws IOException, JSONException, WeatherException {
        OpenWeatherMapWeather weather = new OpenWeatherMapWeather(getContext(), readJSON("omsk_name_2.5.json"));
        WeatherCondition condition = weather.getConditions().get(0);
        assertEquals(21, condition.getTemperature(TemperatureUnit.C).getHigh());
    }

    public void testGetWind() throws IOException, JSONException, WeatherException {
        OpenWeatherMapWeather weather = new OpenWeatherMapWeather(getContext(), readJSON("omsk_name_2.5.json"));
        WeatherCondition condition = weather.getConditions().get(0);
        Wind wind = condition.getWind(WindSpeedUnit.MPS);
        assertNotNull(wind);
        assertEquals(4, wind.getSpeed());
        assertEquals(WindDirection.N, wind.getDirection());
    }

    public void testGetHumidity() throws IOException, JSONException, WeatherException {
        OpenWeatherMapWeather weather = new OpenWeatherMapWeather(getContext(), readJSON("omsk_name_2.5.json"));
        WeatherCondition condition = weather.getConditions().get(0);
        Humidity humidity = condition.getHumidity();
        assertNotNull(humidity);
        assertEquals(56, humidity.getValue());
    }

    public void testGetTemperatureUnit() throws IOException, JSONException, WeatherException {
        OpenWeatherMapWeather weather = new OpenWeatherMapWeather(getContext(), readJSON("omsk_name_2.5.json"));
        WeatherCondition condition = weather.getConditions().get(0);
        assertEquals(TemperatureUnit.K, condition.getTemperature().getTemperatureUnit());
    }

    public void testGetCityID() throws IOException, JSONException, WeatherException {
        OpenWeatherMapWeather weather = new OpenWeatherMapWeather(getContext(), readJSON("omsk_name_2.5.json"));
        assertEquals(1496153, weather.getCityId());
    }

    public void testGetForecastURL() throws IOException, JSONException, WeatherException {
        OpenWeatherMapWeather weather = new OpenWeatherMapWeather(getContext(), readJSON("omsk_name_2.5.json"));
        assertEquals(new URL("http://m.openweathermap.org/city/1496153#forecast"), weather.getForecastURL());
    }

    public void testForecastsNulls() throws IOException, JSONException, WeatherException {
        OpenWeatherMapWeather weather = new OpenWeatherMapWeather(getContext(), readJSON("omsk_name_2.5.json"));
        weather.parseDailyForecast(readJSON("omsk_name_forecast_2.5.json"));
        assertEquals(4, weather.getConditions().size());
        assertNotNull(weather.getConditions().get(3).getHumidity());
        assertNotNull(weather.getConditions().get(3).getWind());
    }

    public void testForecastGetLowTemperature() throws IOException, JSONException, WeatherException {
        OpenWeatherMapWeather weather = new OpenWeatherMapWeather(getContext(), readJSON("omsk_name_2.5.json"));
        weather.parseDailyForecast(readJSON("omsk_name_forecast_2.5.json"));
        List<WeatherCondition> conditions = weather.getConditions();
        assertEquals(4, conditions.size());
        assertEquals(287, conditions.get(0).getTemperature(TemperatureUnit.K).getLow());
        assertEquals(284, conditions.get(1).getTemperature(TemperatureUnit.K).getLow());
        assertEquals(282, conditions.get(2).getTemperature(TemperatureUnit.K).getLow());
        assertEquals(283, conditions.get(3).getTemperature(TemperatureUnit.K).getLow());
    }

    public void testForecastGetHighTemperature() throws IOException, JSONException, WeatherException {
        OpenWeatherMapWeather weather = new OpenWeatherMapWeather(getContext(), readJSON("omsk_name_2.5.json"));
        weather.parseDailyForecast(readJSON("omsk_name_forecast_2.5.json"));
        List<WeatherCondition> conditions = weather.getConditions();
        assertEquals(4, conditions.size());
        assertEquals(294, conditions.get(0).getTemperature(TemperatureUnit.K).getHigh());
        assertEquals(293, conditions.get(1).getTemperature(TemperatureUnit.K).getHigh());
        assertEquals(293, conditions.get(2).getTemperature(TemperatureUnit.K).getHigh());
        assertEquals(295, conditions.get(3).getTemperature(TemperatureUnit.K).getHigh());
    }

    public void testForecastGetTemperature() throws IOException, JSONException, WeatherException {
        OpenWeatherMapWeather weather = new OpenWeatherMapWeather(getContext(), readJSON("omsk_name_2.5.json"));
        weather.parseDailyForecast(readJSON("omsk_name_forecast_2.5.json"));
        List<WeatherCondition> conditions = weather.getConditions();
        assertEquals(4, conditions.size());
        //the current temp should come from the city JSON
        assertEquals(294, conditions.get(0).getTemperature(TemperatureUnit.K).getCurrent());
    }

    public void testForecastGetPrecipitations() throws IOException, JSONException, WeatherException {
        OpenWeatherMapWeather weather = new OpenWeatherMapWeather(getContext(), readJSON("omsk_name_2.5.json"));
        weather.parseDailyForecast(readJSON("omsk_name_forecast_2.5.json"));
        List<SimpleWeatherCondition> conditions = weather.getOpenWeatherMapConditions();
        assertEquals(4, conditions.size());
        assertEquals(0f, conditions.get(0).getPrecipitation().getValue(PrecipitationPeriod.PERIOD_1H));  //current
        assertEquals(1f, conditions.get(1).getPrecipitation().getValue(PrecipitationPeriod.PERIOD_1H), 0.01f);
        assertEquals(2f, conditions.get(2).getPrecipitation().getValue(PrecipitationPeriod.PERIOD_1H), 0.01f);
        assertEquals(3f, conditions.get(3).getPrecipitation().getValue(PrecipitationPeriod.PERIOD_1H), 0.01f);
    }

    public void testForecastGetCloudiness() throws IOException, JSONException, WeatherException {
        OpenWeatherMapWeather weather = new OpenWeatherMapWeather(getContext(), readJSON("omsk_name_2.5.json"));
        weather.parseDailyForecast(readJSON("omsk_name_forecast_2.5.json"));
        List<SimpleWeatherCondition> conditions = weather.getOpenWeatherMapConditions();
        assertEquals(4, conditions.size());
        assertEquals(0, conditions.get(0).getCloudiness().getValue());  //current
        assertEquals(18, conditions.get(1).getCloudiness().getValue());
        assertEquals(0, conditions.get(2).getCloudiness().getValue());
        assertEquals(22, conditions.get(3).getCloudiness().getValue());
    }

    public void testForecastGetConditionTypes() throws IOException, JSONException, WeatherException {
        OpenWeatherMapWeather weather = new OpenWeatherMapWeather(getContext(), readJSON("omsk_name_2.5.json"));
        weather.parseDailyForecast(readJSON("omsk_name_forecast_2.5.json"));
        List<SimpleWeatherCondition> conditions = weather.getOpenWeatherMapConditions();
        assertEquals(4, conditions.size());
        assertEquals(1, conditions.get(0).getConditionTypes().size());
        assertTrue(conditions.get(0).getConditionTypes().contains(WeatherConditionType.CLOUDS_BROKEN));
        assertEquals(2, conditions.get(1).getConditionTypes().size());
        assertTrue(conditions.get(1).getConditionTypes().contains(WeatherConditionType.CLOUDS_OVERCAST));
        assertTrue(conditions.get(1).getConditionTypes().contains(WeatherConditionType.RAIN_LIGHT));
        assertEquals(2, conditions.get(2).getConditionTypes().size());
        assertTrue(conditions.get(2).getConditionTypes().contains(WeatherConditionType.CLOUDS_OVERCAST));
        assertTrue(conditions.get(2).getConditionTypes().contains(WeatherConditionType.RAIN));
        assertEquals(2, conditions.get(3).getConditionTypes().size());
        assertTrue(conditions.get(3).getConditionTypes().contains(WeatherConditionType.CLOUDS_OVERCAST));
        assertTrue(conditions.get(3).getConditionTypes().contains(WeatherConditionType.RAIN_LIGHT));
    }

    public void testForecastGetConditionText() throws IOException, JSONException, WeatherException {
        OpenWeatherMapWeather weather = new OpenWeatherMapWeather(getContext(), readJSON("omsk_name_2.5.json"));
        weather.parseDailyForecast(readJSON("omsk_name_forecast_2.5.json"));
        List<WeatherCondition> conditions = weather.getConditions();
        assertEquals(4, conditions.size());
        assertEquals("Broken clouds", conditions.get(0).getConditionText());
        assertEquals("Light rain", conditions.get(1).getConditionText());
        assertEquals("Rain", conditions.get(2).getConditionText());
        assertEquals("Light rain", conditions.get(3).getConditionText());
    }

    public void testParseNoHumidity() throws IOException, JSONException, WeatherException {
        OpenWeatherMapWeather weather = new OpenWeatherMapWeather(getContext(), readJSON("omsk_city_no_humidity.json"));
        assertNotNull(weather);
        assertFalse(weather.isEmpty());
        WeatherCondition condition = weather.getConditions().get(0);
        assertEquals(4, condition.getTemperature(TemperatureUnit.C).getCurrent());
    }

    public void testParseMinimalJSON() throws IOException, JSONException, WeatherException {
        JSONTokener parser = new JSONTokener("{\"list\":[\n" +
                "    {\n" +
                "        \"id\":1496153,\n" +
                "    }\n" +
                "]}");
        OpenWeatherMapWeather weather = new OpenWeatherMapWeather(getContext(), (JSONObject)parser.nextValue());
        assertNotNull(weather);
        assertFalse(weather.isEmpty());
        assertEquals(1496153, weather.getCityId());
        assertEquals("", weather.getLocation().getText());
        WeatherCondition condition = weather.getConditions().get(0);
        assertEquals(Temperature.UNKNOWN, condition.getTemperature(TemperatureUnit.C).getCurrent());
    }

    public void testParseBadResultCodeJSON() throws JSONException, WeatherException {
        JSONTokener parser = new JSONTokener("{ \"cod\": \"404\"}");
        OpenWeatherMapWeather weather = new OpenWeatherMapWeather(getContext(), (JSONObject)parser.nextValue());
        assertNotNull(weather);
        assertTrue(weather.isEmpty());
    }

    JSONObject readJSON(String resourceName) throws IOException, JSONException {
        Reader reader = new InputStreamReader(getClass().getResourceAsStream(resourceName));
        StringBuilder buffer = new StringBuilder();
        int c = reader.read();
        while (c >= 0) {
            buffer.append((char)c);
            c = reader.read();
        }
        JSONTokener parser = new JSONTokener(buffer.toString());
        return (JSONObject)parser.nextValue();
    }

}
