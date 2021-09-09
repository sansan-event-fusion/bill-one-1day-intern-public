import React from "react";
import "./App.css";
import { AdvancedExerciseContextProvider } from "./context/advanced-exercise-context";
import { AppRouter } from "./router";
import { ThemeProvider } from "./theme";

function App() {
  return (
    <ThemeProvider>
      <AdvancedExerciseContextProvider>
        <AppRouter />
      </AdvancedExerciseContextProvider>
    </ThemeProvider>
  );
}

export default App;
