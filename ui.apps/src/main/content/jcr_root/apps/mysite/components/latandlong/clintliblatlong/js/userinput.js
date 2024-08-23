document.addEventListener('DOMContentLoaded', function() {
    document.getElementById('getweather').addEventListener('click', function(e) {
        e.preventDefault();
        // Get user input for latitude and longitude
        const lat = document.getElementById('latitude').value;
        console.log(lat)
        const lon = document.getElementById('longitude').value;
        console.log(lon)

        if (lat && lon) {
            const url = `http://localhost:6502/bin/weatherdata?lat=${lat}&lon=${lon}`;
            
            fetch(url)
                .then((response) => {
                    console.log("API Response:", response.json());
                    //return response.json();
                })
                .then((data) => {
                    console.log("Weather Data:", data);

                    // Update the weather icon
                    const weatherIconElement = document.getElementById("icon");
                    if (weatherIconElement) {
                        weatherIconElement.src = `http://openweathermap.org/img/w/${data.icon}.png`;
                        console.log("Icon updated:", weatherIconElement.src);
                    } else {
                        console.error("weather-icon element not found.");
                    }

                    // Update the temperature
                    const temperatureElement = document.getElementById("temp");
                    if (temperatureElement) {
                        temperatureElement.innerText = `${data.temperature} °F`;
                        console.log("Temperature updated:", temperatureElement.innerText);
                    } else {
                        console.error("temperature element not found.");
                    }

                    // Update the pressure
                    const pressureElement = document.getElementById("pssure");
                    if (pressureElement) {
                        pressureElement.innerText = `${data.pressure} hPa`;
                        console.log("Pressure updated:", pressureElement.innerText);
                    } else {
                        console.error("pressure element not found.");
                    }

                    // Update other fields
                    const description = document.getElementById("desc");
                    const climateElement = document.getElementById("cli");
                    const cityElement = document.getElementById("city");
                    const countryElement = document.getElementById("country");
                    const minTempElement = document.getElementById("mintemp");
                    const maxTempElement = document.getElementById("maxtemp");
                    const feelsLikeElement = document.getElementById("feelslike");
                    const humidityElement = document.getElementById("humid");
                    const windSpeedElement = document.getElementById("windspeed");
                    const date = document.getElementById("date");
                    const time = document.getElementById("time");
                    const sunrise = document.getElementById("sunrise");
                    const sunset = document.getElementById("sunset");

                    if (sunrise) description.innerText= `${data.sunrise}`;
                    if (sunset) description.innerText= `${data.sunset}`;
                    if (time) description.innerText= `${data.time}`;
                    if (date) description.innerText= `${data.date}`;
                    if (description) description.innerText= `${data.description}`;
                    if (climateElement) climateElement.innerText = `${data.climate}`;
                    if (cityElement) cityElement.innerText = `${data.placeName}`;
                    if (countryElement) countryElement.innerText = `${data.country}`;
                    if (minTempElement) minTempElement.innerText = `Min: ${data.min}`;
                    if (maxTempElement) maxTempElement.innerText = `Max: ${data.max}`;
                    if (feelsLikeElement) feelsLikeElement.innerText = `${data.feelslike} °F`;
                    if (humidityElement) humidityElement.innerText = `${data.humidity} %`;
                    if (windSpeedElement) windSpeedElement.innerText = `${data.windspeed} mph`;

                    // Log the elements to check if the content is updated
                    console.log("City:", cityElement?.innerText);
                    console.log("Country:", countryElement?.innerText);
                    console.log("Temperature:", temperatureElement?.innerText);
                    console.log("Pressure:", pressureElement?.innerText);
                    console.log("Icon:", weatherIconElement?.src);
                    console.log("climate:", climateElement?.innerText);

                })
                .catch(error => {
                    console.error("Error fetching weather data: ", error);
                });
        } else {
            console.error("Please enter both latitude and longitude.");
        }
    });
});
