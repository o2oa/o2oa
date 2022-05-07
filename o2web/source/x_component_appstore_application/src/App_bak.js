import logo from './logo.svg';
import o2logo from './o2logo.png';
import './App.css';
import {lp, o2, component} from '@o2oa/component'

function App() {
  function openCalendar(){
    o2.api.page.openApplication("Calendar");
  }
  function openOrganization(){
    o2.api.page.openApplication("Org");
  }
  function openInBrowser() {
    component.openInNewBrowser(true);
  }
  function startProcess(){
    o2.api.page.startProcess();
  }
  function createDocument(){
    o2.api.page.createDocument();
  }
  return (
      <div className="App">
        <header className="App-header">
          <div>
          <img src={o2logo} className="App-p2logo" alt="logo" />
          <img src={logo} className="App-logo" alt="logo" />
          </div>
          <p>
            {lp.welcome}
          </p>
          <div>
            <a
                className="App-link"
                href="https://reactjs.org"
                target="_blank"
                rel="noopener noreferrer"
            >
              Learn React
            </a>
            <a
                className="App-link"
                href="https://www.o2oa.net/develop.html"
                target="_blank"
                rel="noopener noreferrer"
            >
              Learn O2OA
            </a>
          </div>
          <br/>
          <div>
            <button onClick={openCalendar}>{lp.openCalendar}</button>
            <button onClick={openOrganization}>{lp.openOrganization}</button>
            <button onClick={startProcess}>{lp.startProcess}</button>
            <button onClick={createDocument}>{lp.createDocument}</button>
            <br/>
            <button onClick={openInBrowser}>{lp.openInBrowser}</button>
          </div>
        </header>
      </div>
  );
}

export default App;
