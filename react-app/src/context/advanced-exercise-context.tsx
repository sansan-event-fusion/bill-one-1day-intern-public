import React, { useContext } from "react";

type AdvancedExerciseContextProps = {
  isAdvancedExerciseEnabled: boolean;
};

const AdvancedExerciseContext =
  React.createContext<AdvancedExerciseContextProps>({
    isAdvancedExerciseEnabled: false,
  });

export const AdvancedExerciseContextProvider: React.VFC<{
  children: React.ReactChild;
}> = ({ children }) => {
  return (
    <AdvancedExerciseContext.Provider
      value={{
        isAdvancedExerciseEnabled:
          process.env.REACT_APP_ADVANCED_EXERCISE === "true",
      }}
    >
      {children}
    </AdvancedExerciseContext.Provider>
  );
};

export const useAdvancedExerciseContext = (): AdvancedExerciseContextProps => {
  return useContext(AdvancedExerciseContext);
};
