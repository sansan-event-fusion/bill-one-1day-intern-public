import React from "react";
import {
  BrowserRouter as Router,
  Redirect,
  Route,
  Switch,
  useRouteMatch,
} from "react-router-dom";
import { RecipientInvoices } from "./components/pages/recipient/invoices";
import { SenderInvoices } from "./components/pages/sender/invoices";
import { SenderSendInvoice } from "./components/pages/sender/send-invoice";

const SenderRouter: React.FC = () => {
  const { path } = useRouteMatch();

  return (
    <Switch>
      <Route exact path={path}>
        <Redirect to={`${path}/send`} />
      </Route>
      <Route exact path={`${path}/send`}>
        <SenderSendInvoice />
      </Route>
      <Route exact path={`${path}/invoices`}>
        <SenderInvoices />
      </Route>
    </Switch>
  );
};

export const AppRouter: React.FC = () => {
  return (
    <Router>
      <Switch>
        <Route exact path="/">
          <Redirect to="/sender" />
        </Route>
        <Route exact path="/recipient">
          <RecipientInvoices />
        </Route>
        <Route path="/sender">
          <SenderRouter />
        </Route>
      </Switch>
    </Router>
  );
};
