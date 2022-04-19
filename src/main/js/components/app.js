import React from 'react';
import Header from './header';
import { Switch, Route } from 'react-router-dom';
import { connect } from "react-redux";

import PetInfo from './pages/PetInfo';
import Inventory from './pages/Inventory';
import Goal from './pages/Goal';
import Shop from './pages/Shop';

import { updatePet } from '../stores/petSlice';
import { updateInventory } from '../stores/inventorySlice';
import { updateShop } from '../stores/shopSlice';
import { updateUser } from '../stores/userSlice';

const mapDispatchToProps = (dispatch) => {
	return {
		updateAll: () => {
			dispatch(updatePet());
			dispatch(updateInventory());
			dispatch(updateShop());
			dispatch(updateUser());
		}
	}
}

class App extends React.Component {
	componentDidMount() {
		this.props.updateAll();
	}
	render() {
		return (
			<div>
				<Header/>
				<Switch>
					<Route path='/PetInfo'>
						<PetInfo/>
					</Route>
					<Route path='/Goal'>
						<Goal/>
					</Route>
					<Route path='/Shop'>
						<Shop/>
					</Route>
					<Route path='/Inventory'>
						<Inventory/>
					</Route>
				</Switch>
			</div>
		)
	}
}

export default connect(null,mapDispatchToProps)(App);
