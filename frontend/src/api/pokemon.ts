import client from './client';
import type { PagedResult, PokemonListItem, PokemonDetail, LocalPokemon, AuthResponse, UpdatePokemonRequest } from '../types';

export const fetchPokemonList = async (page = 0, size = 20): Promise<PagedResult<PokemonListItem>> => {
  const { data } = await client.get(`/api/pokemon?page=${page}&size=${size}`);
  return data;
};

export const fetchPokemonDetail = async (nameOrId: string): Promise<PokemonDetail> => {
  const { data } = await client.get(`/api/pokemon/${nameOrId}`);
  return data;
};

export const syncPokemon = async (nameOrId: string): Promise<LocalPokemon> => {
  const { data } = await client.post(`/api/pokemon/${nameOrId}/sync`);
  return data;
};

export const fetchLocalPokemon = async (): Promise<LocalPokemon[]> => {
  const { data } = await client.get('/api/pokemon/local');
  return data;
};

export const updateLocalPokemon = async (id: string, request: UpdatePokemonRequest): Promise<LocalPokemon> => {
  const { data } = await client.put(`/api/pokemon/local/${id}`, request);
  return data;
};

export const deleteLocalPokemon = async (id: string): Promise<void> => {
  await client.delete(`/api/pokemon/local/${id}`);
};

export const login = async (username: string, password: string): Promise<AuthResponse> => {
  const { data } = await client.post('/api/auth/login', { username, password });
  return data;
};

export const register = async (username: string, email: string, password: string): Promise<void> => {
  await client.post('/api/auth/register', { username, email, password });
};
