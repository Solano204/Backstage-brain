import React from 'react';
import { CatalogIcon, Content, DocsIcon, Header, Page } from '@backstage/core-components';
import { Grid, List, Paper } from '@material-ui/core';

import {
  CatalogSearchResultListItem,
} from '@backstage/plugin-catalog';
import {
  SearchBar,
  SearchFilter,
  SearchResult,
  SearchPagination,
  useSearch,
} from '@backstage/plugin-search-react';
import {
  TechDocsSearchResultListItem,
  techdocsApiRef,
} from '@backstage/plugin-techdocs';
import { SearchType } from '@backstage/plugin-search';

const SearchPage = () => {
  const { types } = useSearch();

  return (
    <Page themeId="home">
      <Header title="Search" />
      <Content>
        <Grid container direction="row">
          <Grid item xs={12}>
            <Paper>
              <SearchBar />
            </Paper>
          </Grid>
          <Grid item xs={3}>
            <SearchType.Accordion
              name="Result Type"
              defaultValue="software-catalog"
              types={[
                {
                  value: 'software-catalog',
                  name: 'Software Catalog',
                  icon: <CatalogIcon />,
                },
                {
                  value: 'techdocs',
                  name: 'Documentation',
                  icon: <DocsIcon />,
                },
              ]}
            />
            <Paper>
              <SearchFilter.Select
                label="Kind"
                name="kind"
                values={['Component', 'Template', 'API']}
              />
              <SearchFilter.Checkbox
                label="Lifecycle"
                name="lifecycle"
                values={['experimental', 'production', 'deprecated']}
              />
            </Paper>
          </Grid>
          <Grid item xs={9}>
            <SearchPagination />
            <SearchResult>
              <CatalogSearchResultListItem icon={<CatalogIcon />} />
              <TechDocsSearchResultListItem icon={<DocsIcon />} />
            </SearchResult>
          </Grid>
        </Grid>
      </Content>
    </Page>
  );
};

export const searchPage = <SearchPage />;