import React, { PropsWithChildren } from 'react';
import { makeStyles } from '@material-ui/core';
import HomeIcon from '@material-ui/icons/Home';
import ExtensionIcon from '@material-ui/icons/Extension';
import MapIcon from '@material-ui/icons/MyLocation';
import LibraryBooks from '@material-ui/icons/LibraryBooks';
import CreateComponentIcon from '@material-ui/icons/AddCircleOutline';
import {
  Settings as SidebarSettings,
  UserSettingsSignInAvatar,
} from '@backstage/plugin-user-settings';
import { SidebarSearchModal } from '@backstage/plugin-search';
import {
  Sidebar,
  sidebarConfig,
  SidebarDivider,
  SidebarGroup,
  SidebarItem,
  SidebarPage,
  SidebarScrollWrapper,
  SidebarSpace,
  useSidebarOpenState,
  Link,
  SidebarSubmenu,
  SidebarSubmenuItem,
} from '@backstage/core-components';
import MenuIcon from '@material-ui/icons/Menu';
import SearchIcon from '@material-ui/icons/Search';
import CategoryIcon from '@material-ui/icons/Category';
import RegisterComponentIcon from '@material-ui/icons/PostAdd';
import LogoFull from './LogoFull';
import LogoIcon from './LogoIcon';

const useSidebarLogoStyles = makeStyles({
  root: {
    width: sidebarConfig.drawerWidthClosed,
    height: 3 * sidebarConfig.logoHeight,
    display: 'flex',
    flexFlow: 'row nowrap',
    alignItems: 'center',
    marginBottom: -14,
  },
  link: {
    width: sidebarConfig.drawerWidthClosed,
    marginLeft: 24,
  },
});

const SidebarLogo = () => {
  const classes = useSidebarLogoStyles();
  const { isOpen } = useSidebarOpenState();

  return (
    <div className={classes.root}>
      <Link to="/" underline="none" className={classes.link} aria-label="Home">
        {isOpen ? <LogoFull /> : <LogoIcon />}
      </Link>
    </div>
  );
};

export const Root = ({ children }: PropsWithChildren<{}>) => (
  <SidebarPage>
    <Sidebar>
      <SidebarLogo />
      <SidebarGroup label="Search" icon={<SearchIcon />} to="/search">
        <SidebarSearchModal />
      </SidebarGroup>
      <SidebarDivider />
      <SidebarGroup label="Menu" icon={<MenuIcon />}>
        {/* Catalog */}
        <SidebarItem icon={HomeIcon} to="catalog" text="Catalog">
          <SidebarSubmenu title="Catalog">
            <SidebarSubmenuItem
              title="Components"
              to="catalog?filters[kind]=component"
              icon={ExtensionIcon}
            />
            <SidebarSubmenuItem
              title="APIs"
              to="catalog?filters[kind]=api"
              icon={ExtensionIcon}
            />
            <SidebarSubmenuItem
              title="Systems"
              to="catalog?filters[kind]=system"
              icon={CategoryIcon}
            />
            <SidebarSubmenuItem
              title="Domains"
              to="catalog?filters[kind]=domain"
              icon={CategoryIcon}
            />
            <SidebarSubmenuItem
              title="Resources"
              to="catalog?filters[kind]=resource"
              icon={ExtensionIcon}
            />
          </SidebarSubmenu>
        </SidebarItem>

        {/* API Docs */}
        <SidebarItem icon={ExtensionIcon} to="api-docs" text="APIs" />

        {/* Documentation */}
        <SidebarItem icon={LibraryBooks} to="docs" text="Docs" />

        {/* Create (Templates) */}
        <SidebarItem
          icon={CreateComponentIcon}
          to="create"
          text="Create..."
        />

        {/* Register Component */}
        <SidebarItem
          icon={RegisterComponentIcon}
          to="catalog-import"
          text="Register"
        />

        <SidebarDivider />

        {/* Catalog Graph */}
        <SidebarItem icon={MapIcon} to="catalog-graph" text="Graph" />

        {/* Tech Radar */}
        <SidebarItem icon={MapIcon} to="tech-radar" text="Tech Radar" />
      </SidebarGroup>
      <SidebarSpace />
      <SidebarDivider />
      <SidebarGroup
        label="Settings"
        icon={<UserSettingsSignInAvatar />}
        to="/settings"
      >
        <SidebarSettings />
      </SidebarGroup>
    </Sidebar>
    {children}
  </SidebarPage>
);