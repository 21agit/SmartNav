# Navigations-App mit Google Maps API (Android)

Ich habe diese Navigations-App in Kotlin programmiert, die die GPS-Ortung nutzt, um den aktuellen 
Standort in Echtzeit zu ermitteln. Nach Ablauf eines Countdowns berechnet die App automatisch die 
Route zum nächstgelegenen Parkplatz. Dabei habe ich neben der Google Maps-API auch die Google 
Places- und Google Directions-API eingebunden.

## Features

- **GPS-Positionierung**: Erkennt den aktuellen Standort in Echtzeit.
- **Countdown**: Löst nach Ablauf eines Timers automatisch die Suche nach dem nächsten Parkplatz
und die Routenberechnung aus.
- **Routenberechnung**: Integriert die Google Maps-, Google Places- und Google Directions-APIs,
um eine Karte anzuzeigen, den nächsten Parkplatz zu ermitteln und die Route zu zeichnen.

## Demo-Video
![](./assets/demovid.gif)

## Nächster Schritt

Ich werde als nächsten Schritt die App in einen Android Auto Emulator integrieren. Sobald die
Gangschaltung in den Fahrmodus „D“ gestellt wird, erkennt mein Programm diese Änderung über die
VHAL-Schnittstelle und startet automatisch den Countdown. Dadurch wird es für den Fahrer
komfortabler, da der Countdown als rechtzeitiger Hinweis dient und verhindert, dass er versehentlich
seine Pausen vergisst.