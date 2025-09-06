import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.jsx'
import AppRouter from './routes/AppRouter.jsx'
import { ThemeProvider } from './theme/ThemeContext.jsx'
// Redux
import { Provider } from "react-redux";
import { store } from "./store/store.js";

createRoot(document.getElementById('root')).render(
  <StrictMode>
    {/* Provider = κάνει διαθέσιμο το Redux store σε όλη την React εφαρμογή */}
    <Provider store={store}>
      <ThemeProvider>{/* Context Dark-Light theme*/}
        <AppRouter />
      </ThemeProvider>
    </Provider>
  </StrictMode>,
)
