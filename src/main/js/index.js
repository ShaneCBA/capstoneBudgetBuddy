import React from 'react';
import ReactDOM from 'react-dom';
import { Provider } from 'react-redux';
import { HashRouter } from 'react-router-dom';
import Modal from 'react-modal';

import App from './components/app';

import store from './store'

Modal.setAppElement('#app')

ReactDOM.render(
	<Provider store={store}>
		<HashRouter hashType="noslash">
			<App />
		</HashRouter>
	</Provider>,
	document.getElementById('app')
)