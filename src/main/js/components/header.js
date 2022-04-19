import React from 'react';
import { Link } from 'react-router-dom';
import { connect } from 'react-redux';

const mapStateToProps = state => {
	return {userInfo: state.user}
}

class Header extends React.Component {
	render() {
		return (
            <div>
				<div>GAME</div>
				<ul>
					<li><Link to="Shop">Shop</Link></li>
					<li><Link to="Goal">Goal</Link></li>
					<li><Link to="PetInfo">Pet</Link></li>
					<li><Link to="Inventory">Inventory</Link></li>
				</ul>
				<span>Points : {this.props.userInfo.loaded ? this.props.userInfo.user.points : "..."}</span>
			</div>
		)
	}
}

export default connect(mapStateToProps, null)(Header);