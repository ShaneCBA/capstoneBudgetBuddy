import React from "react";
import { connect } from "react-redux";

const mapStateToProps = state => {
    return { user: state.user };
};

class Goal extends React.Component {
    render() {
        return (
            <div>
                <h2>Goal</h2>
                {
                    this.props.user.loaded ? 
                    <ul>
                        <li>Type - {this.props.user.user.goal.type}</li>
                        <li>Target - {this.props.user.user.goal.target}</li>
                        <li>Start Date - {this.props.user.user.goal.startDate}</li>
                    </ul>
                    :
                    "loading..."
                }
            </div>
        )
    }
}

export default connect(mapStateToProps, null)(Goal)