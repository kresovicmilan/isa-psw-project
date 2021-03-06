import React from 'react';
import {withRouter} from 'react-router-dom';

import classes from './HomepageToolbarItems.module.css';
import HomepageToolbarItem from './HomepageToolbarItem/HomepageToolbarItem';
import Button from '../../UI/Button/Button';

const HomepageToolbarItems = (props) => {

    const onLogoutHandler = () => {
        if(localStorage.getItem('token') !== null){
            window.localStorage.removeItem('token');
            props.history.push('/');
        }
    }

    return (
        <ul className={classes.NavigationItems}>
            <HomepageToolbarItem link='/profile' exact>My Profile</HomepageToolbarItem>
            <HomepageToolbarItem link='/homepage' exact>Homepage</HomepageToolbarItem>
            <HomepageToolbarItem link='/clinic-info' exact style={{marginRight: '30px'}}>Clinic Info</HomepageToolbarItem>
            <Button click={onLogoutHandler}>Logout</Button>
        </ul>
    );
};

export default withRouter(HomepageToolbarItems);