export interface PokemonListItem {
  id: number;
  name: string;
  spriteUrl: string;
  category: string;
  massHg: number;
  skills: string[];
}

export interface PagedResult<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export interface PokemonStat {
  [key: string]: number;
}

export interface PokemonDetail {
  id: number;
  name: string;
  spriteUrl: string;
  stats: PokemonStat;
  description: string;
  evolutionChain: string[];
}

export interface LocalPokemon {
  id: string;
  pokeapiId: number;
  name: string;
  localName: string | null;
  category: string;
  massHg: number;
  spriteUrl: string;
  region: string | null;
  tags: string[];
  skills: string[];
}

export interface AuthResponse {
  token: string;
  username: string;
  role: string;
}

export interface UpdatePokemonRequest {
  localName?: string;
  region?: string;
  tags?: string[];
}
