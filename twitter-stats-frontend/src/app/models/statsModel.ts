export class StatsModel {
  bitcoinPositive: number;
  bitcoinNegative: number;
  ethereumPositive: number;
  ethereumNegative: number;

  constructor(bitcoinPositive: number = 0, bitcoinNegative: number = 0, ethereumPositive: number = 0, ethereumNegative: number = 0) {
    this.bitcoinPositive = bitcoinPositive;
    this.bitcoinNegative = bitcoinNegative;
    this.ethereumPositive = ethereumPositive;
    this.ethereumNegative = ethereumNegative;
  }
}
